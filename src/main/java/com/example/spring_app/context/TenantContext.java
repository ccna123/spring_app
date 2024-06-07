package com.example.spring_app.context;

public class TenantContext {

    public static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    public static void setCurrentTenant(String tenant){
        CURRENT_TENANT.set(tenant);
    }

    public static void clear(){
        CURRENT_TENANT.remove();
    }
    
}
