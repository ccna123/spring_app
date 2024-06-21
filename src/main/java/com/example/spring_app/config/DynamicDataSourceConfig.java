package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    SSMConfig ssmConfig;

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
        String[] tenaStrings = { "//schoolA//dbconfig" };

        for (String tenantID : tenaStrings) {
            DataSource dataSource = createDataSource(tenantID);
            if (tenantID != null && dataSource != null) {
                resolvedDataSources.put(tenantID, dataSource);
            }
        }
    }

    private String getTenantNameFromFileName(String tenantFileName) {
        logger.warn("tenantFileName: " + tenantFileName);
        return tenantFileName.substring(0, tenantFileName.lastIndexOf("."));
    }

    private DataSource createDataSource(String tenantID) {

        try {
            String properties = ssmConfig.getParameterValue(tenantID);
            logger.info("parameter: " + properties);
            // logger.info("url: " + properties.get("url"));


            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            // dataSourceBuilder.driverClassName(properties.get("driver-class-name"));
            // dataSourceBuilder.username(properties.get("username"));
            // dataSourceBuilder.password(properties.get("password"));
            // dataSourceBuilder.url(properties.get("url"));

            return dataSourceBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Problem in tenant datasource:" + e);
        }
    }

    private Map<String, String> parseParameterValue(String parameterValue) {
        Map<String, String> properties = new HashMap<>();
        String[] entries = parameterValue.split(",");
        for (String entry : entries) {
            String[] keyValue = entry.split("=");
            properties.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return properties;
    }
}
