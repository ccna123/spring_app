package com.example.spring_app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;

@Configuration
public class FilterConfig {
    
    FilterRegistrationBean<Filter> tenantFilter(){
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter((Filter) new TenantFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
