package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.context.TenantContext;

public class MultitenantDataSource  extends AbstractRoutingDataSource{
    final Logger logger = LoggerFactory.getLogger(MultitenantDataSource.class);
    @Override
    protected Object determineCurrentLookupKey() {
        logger.info("getCurrentTenant: " + TenantContext.getCurrentTenant());
        return TenantContext.getCurrentTenant();
    }
    
}
