package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

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
    
    @Value("${spring.datasource.dynamodbTable}")
    private String dynamodbTable;
    
    @Value("${spring.datasource.region}")
    private String region;

    Map<Object, Object> resolvedDataSources = new HashMap<>();
    final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    private AbstractRoutingDataSource routingDataSource;

    @Bean
    DataSource dataSource() {
        logger.info("DynamicDataSourceConfig get called");
        loadTenantDataSources();
        return routingDatasource();

    }

    public void fetchAndStoreTenantConfigFromDynamoDB(String tenantId){
        logger.info("Go to dynamodb and fetch file");
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                                .standard()
                                .withRegion(region)
                                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(dynamodbTable);

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("tenantID", tenantId);
        try {
            Item item = table.getItem(spec);
            if (item != null) {
                String tenantProperties = item.getString("tenantProperties");
                storeItemLocally(tenantId, tenantProperties);
                reloadTenantDataSource(tenantId);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve item: " + e.getMessage());
        }
    }

    public String getTenantsFilePath() {
        return tenantsFilePath;
    }
    
    private void storeItemLocally(String tenantId, String itemJson){
        try(FileWriter file = new FileWriter(Paths.get(tenantsFilePath, tenantId + ".properties").toString())) {
            file.write(itemJson);
        } catch (IOException e) {
            logger.error("Failed to store item locally", e.getMessage());
        }
    }

    private DataSource routingDatasource() {
        this.routingDataSource = new MultitenantDataSourceRouting();
        routingDataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        routingDataSource.setTargetDataSources(new HashMap<>(resolvedDataSources));
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    public void reloadTenantDataSource(String tenantId) {
        logger.info("reloadTenantDataSource");
        File propertyFile = new File(Paths.get(tenantsFilePath, tenantId + ".properties").toString());
        if (propertyFile.exists()) {
            DataSource dataSource = createDataSource(propertyFile);
            if (dataSource != null) {
                resolvedDataSources.put(tenantId, dataSource);
                routingDataSource.setTargetDataSources(new HashMap<>(resolvedDataSources));
                routingDataSource.afterPropertiesSet();
                logger.info("Reloaded data source for tenant: " + tenantId);
            }
        }
    }

    public boolean isDataSourceAlreadyLoaded(String tenantId){
        logger.info("Datasource is already loaded: " + resolvedDataSources.containsKey(tenantId));
        return resolvedDataSources.containsKey(tenantId);
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
