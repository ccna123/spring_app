package com.example.spring_app.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.example.spring_app.context.TenantContext;

public class MultitenantDataSource  extends AbstractRoutingDataSource{

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
    
}
