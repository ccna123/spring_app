package com.example.spring_app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.context.TenantContext;

//determine the datasource
public class MultitenantDataSourceRouting  extends AbstractRoutingDataSource{
    final Logger logger = LoggerFactory.getLogger(MultitenantDataSourceRouting.class);
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
    
}
