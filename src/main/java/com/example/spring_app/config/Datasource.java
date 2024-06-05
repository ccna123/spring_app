package com.example.spring_app.config;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class Datasource {
    
    @Bean
    DataSource createDataSource(){
        return DataSourceBuilder
        .create()
        .url("jdbc:mysql://localhost:3306/spring?allowPublicKeyRetrieval=true&useSSL=false")
        .username("root")
        .password("root")
        .build();
    }
    
}
