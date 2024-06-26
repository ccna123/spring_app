package com.example.spring_app.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.spring_app.context.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantIntercepter implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-TenantID";
    final Logger logger = LoggerFactory.getLogger(TenantIntercepter.class);

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);
        String filePath = "";
        if (tenantId == null || tenantId.length() == 0) {
            filePath = dynamicDataSourceConfig.getTenantsFilePath() + "\\" + "master" + ".properties";
            System.out.println("-------------------------------------------------------------");
            System.out.println("Tenant header null. Get master tenant");
            if (!isFileExists(filePath)) {
                System.out.println("File does not exist. Fetch and store locally");
                dynamicDataSourceConfig.fetchAndStoreTenantConfigFromDynamoDB("master");
            }
            System.out.println(filePath);
            System.out.println("File already existed. Fetch config locally");
            TenantContext.setCurrentTenant("master");
            return true;
        } else if (tenantId != null) {
            System.out.println("-------------------------------------------------------------");
            System.out.println("Tenant header not null. Get " + tenantId + " tenant");
            filePath = dynamicDataSourceConfig.getTenantsFilePath() + "\\" + tenantId + ".properties";
            if (!isFileExists(filePath)) {
                System.out.println("File does not exist. Fetch and store locally");
                dynamicDataSourceConfig.fetchAndStoreTenantConfigFromDynamoDB(tenantId);
            }
            System.out.println(filePath);
            System.out.println("File already existed. Fetch config locally");
            TenantContext.setCurrentTenant(tenantId);
        }
        return true;
    }

    private boolean isFileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        TenantContext.clear();
    }
}
