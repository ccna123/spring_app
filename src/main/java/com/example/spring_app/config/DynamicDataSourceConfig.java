package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
        Map<String, String> properties = ssmConfig.getParameterByPath("/tenants/");
        parseParameterValue(properties);
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

    private void parseParameterValue(Map<String, String> properties) {
        
        try {
            
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonValue = entry.getValue();

                JsonNode jsonNode = objectMapper.readTree(jsonValue);                
                String fileName = entry.getKey().split("/")[2] + ".properties";

                File file = Paths.get("C:\\allTenants", fileName).toFile();
                try(FileWriter writer = new FileWriter(file)) {
                    writer.write("name=" + jsonNode.get("name").asText()+"\n");
                    writer.write("datasource.url=" + jsonNode.get("url").asText()+"\n");
                    writer.write("datasource.driver-class-name=" + jsonNode.get("driver-class-name").asText()+"\n");
                    writer.write("datasource.username=" + jsonNode.get("username").asText()+"\n");
                    writer.write("datasource.password=" + jsonNode.get("password").asText()+"\n");
                } catch (IOException e) {
                    logger.error("IOException: ", e);
                }
                System.out.println("Written to file: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
