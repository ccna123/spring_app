package com.example.spring_app.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.spring_app.context.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantIntercepter implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-TenantID";

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            if (!Files.exists(Paths.get(dynamicDataSourceConfig.getTenantsFilePath(), tenantId + ".properties"))) {
                dynamicDataSourceConfig.fetchAndStoreTenantConfigFromDynamoDB(tenantId);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        TenantContext.clear();
    }
}
