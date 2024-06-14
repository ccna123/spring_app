package com.example.spring_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TomcatConfig{
    
    @Autowired
    private Environment environment;

    public void verifyTomcatConfig(){
        System.out.println("------------------------TOMCAT CONFIG------------------------");
        System.out.println("Thread max: " + environment.getProperty("server.tomcat.threads.max"));
        System.out.println("Thread min spare: " + environment.getProperty("server.tomcat.threads.min-spare"));
        System.out.println("Max connections: " + environment.getProperty("server.tomcat.max-connections"));
        System.out.println("Queue length: " + environment.getProperty("server.tomcat.accept-count"));
        System.out.println("Connection timeout: " + environment.getProperty("server.tomcat.connection-timeout"));
        System.out.println("TCP-connection-keep-alive-timeout: " + environment.getProperty("server.tomcat.keep-alive-timeout"));
        System.out.println("Maximum-keep-alive-request: " + environment.getProperty("server.tomcat.max-keep-alive-requests"));
    }
}
