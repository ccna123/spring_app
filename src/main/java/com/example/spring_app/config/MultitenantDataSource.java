package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.context.TenantContext;

public class MultitenantDataSource  extends AbstractRoutingDataSource{

    @Override
    protected Object determineCurrentLookupKey() {
        String currentTenant = TenantContext.getCurrentTenant();
        final Logger logger = LoggerFactory.getLogger(MultitenantDataSource.class);
        logger.info("Logger: ", currentTenant);
        return currentTenant;
    }
    
}
