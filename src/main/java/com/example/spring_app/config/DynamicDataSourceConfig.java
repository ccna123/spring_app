package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
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

    private AbstractRoutingDataSource routingDataSource;

    @Bean
    DataSource dataSource() {
        loadTenantDataSources();
        return routingDatasource();

    }

    private DataSource routingDatasource() {
        this.routingDataSource = new MultitenantDataSource();
        routingDataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        routingDataSource.setTargetDataSources(new HashMap<>(resolvedDataSources));

        routingDataSource.afterPropertiesSet();
        return routingDataSource;
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
