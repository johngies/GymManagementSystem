package com.gym.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gymOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Management System API")
                        .description("Production-ready RESTful backend service for managing gym memberships, trainers, scheduled classes, and booking capacity enforcement.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gym Management Development Team")));
    }
}