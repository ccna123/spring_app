package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.controller.RestController.StudentController;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DynamicDataSourceConfig {

    private final String defaultTenant = "master";
    final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    @Bean
    DataSource dataSource() {
        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(createDefaultDataSource());
        dataSource.setTargetDataSources(loadTenantDataSources());
        dataSource.afterPropertiesSet();
        return dataSource;
    }

    private DataSource createDefaultDataSource() {
        File defaultTenantFile = new File("allTenants", defaultTenant + ".properties");
        if (!defaultTenantFile.exists()) {
            throw new RuntimeException("Default tenant file not found: " + defaultTenantFile.getAbsolutePath());
        }

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        Properties defaultTenantProps = loadProperties(defaultTenantFile);

        dataSourceBuilder.driverClassName(defaultTenantProps.getProperty("datasource.driver-class-name"));
        dataSourceBuilder.username(defaultTenantProps.getProperty("datasource.username"));
        dataSourceBuilder.password(defaultTenantProps.getProperty("datasource.password"));
        dataSourceBuilder.url(defaultTenantProps.getProperty("datasource.url"));

        return dataSourceBuilder.build();
    }

    private Map<Object, Object> loadTenantDataSources() {
        File[] tenantFiles = new File("allTenants").listFiles();
        if (tenantFiles == null || tenantFiles.length == 0) {
            throw new RuntimeException("No tenant files found in allTenants folder.");
        }

        Map<Object, Object> dataSources = new HashMap<>();
        for (File tenantFile : tenantFiles) {
            if (!tenantFile.isFile()) {
                continue;
            }
            Properties tenantProps = loadProperties(tenantFile);

            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(tenantProps.getProperty("datasource.driver-class-name"));
            dataSourceBuilder.username(tenantProps.getProperty("datasource.username"));
            dataSourceBuilder.password(tenantProps.getProperty("datasource.password"));
            dataSourceBuilder.url(tenantProps.getProperty("datasource.url"));

            String tenantName = tenantProps.getProperty("name");
            dataSources.put(tenantName, dataSourceBuilder.build());
        }

        return dataSources;
    }

    private Properties loadProperties(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file: " + file.getAbsolutePath(), e);
        }
    }
}
