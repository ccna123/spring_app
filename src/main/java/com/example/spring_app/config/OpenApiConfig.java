package com.example.spring_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OpenAPI for management school")
                        .version("1.0")
                        .description("OpenAPI Documentation for learning")
                        .termsOfService("Terms of service")
                        .contact(new Contact().name("Thanh").email("thanh@abc.com").url("https://thanh.example.local"))
                        .license(new License().name("License name").url("https://license.com")))
                .addServersItem(
                        new io.swagger.v3.oas.models.servers.Server().description("Local").url("http://localhost:8080"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server().description("Production")
                        .url("https://prod:8080"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
