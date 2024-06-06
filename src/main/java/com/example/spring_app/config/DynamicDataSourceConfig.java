package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.controller.RestController.StudentController;

import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DynamicDataSourceConfig {

    @Value("${spring.datasource.defaultTenant}")
    private String defaultTenant;

    @Value("${spring.datasource.tenantsFilePath}")
    private String tenantsFilePath;

    Map<Object, Object> resolvedDataSources = new HashMap<>();
    final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    @Bean
    DataSource dataSource() {
        loadTenantDataSources();
        watchForTenantPropertiesFiles();
        return routingDatasource();

    }

    private void watchForTenantPropertiesFiles() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(tenantsFilePath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            new Thread(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path createdFile = (Path) event.context();
                                File file = path.resolve(createdFile).toFile();
                                if (file.getName().endsWith(".properties")) {
                                    String tenantId = getTenantNameFromFileName(file.getName());
                                    if (tenantId != null) {
                                        DataSource dataSource = createDataSource(file);
                                        resolvedDataSources.put(tenantId, dataSource);
                                        logger.warn("resolvedDataSources: " + resolvedDataSources);
                                    }
                                }
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataSource routingDatasource() {
        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

    private void loadTenantDataSources() {
        File[] files = Paths.get(tenantsFilePath).toFile().listFiles();
        if (files != null) {
            for (File propertyFile : files) {
                if (propertyFile.isFile() && propertyFile.getName().endsWith(".properties")) {
                    String tenantId = getTenantNameFromFileName(propertyFile.getName());
                    DataSource dataSource = createDataSource(propertyFile);

                    if (tenantId != null && dataSource != null) {
                        resolvedDataSources.put(tenantId, dataSource);
                    }
                }
            }
        }
    }

    private String getTenantNameFromFileName(String tenantFileName) {
        logger.warn("tenantFileName: " + tenantFileName);
        return tenantFileName.substring(0, tenantFileName.lastIndexOf("."));
    }

    private DataSource createDataSource(File propertyFile) {

        try (FileInputStream fis = new FileInputStream(propertyFile)) {
            Properties properties = new Properties();
            properties.load(fis);

            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(properties.getProperty("datasource.driver-class-name"));
            dataSourceBuilder.username(properties.getProperty("datasource.username"));
            dataSourceBuilder.password(properties.getProperty("datasource.password"));
            dataSourceBuilder.url(properties.getProperty("datasource.url"));

            return dataSourceBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException("Problem in tenant datasource:" + e);
        }
    }
}
