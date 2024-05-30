package com.example.spring_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Thanh",
            email = "thanh@abc.com",
            url = "https://thanh.example.local"
        ),
        description = "OpenAPI Documentation for learning",
        title = "OpenAPI for management school",
        version = "1.0",
        license = @License(
            name = "License name",
            url = "https://license.com"
        ),
        termsOfService = "Terms of service"
    ),
    servers = {
        @Server(
            description = "Local",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Production",
            url = "https://prod:8080"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class ApiConfig {
    
}
