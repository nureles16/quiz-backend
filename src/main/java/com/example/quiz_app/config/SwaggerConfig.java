package com.example.quiz_app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QuizApp API Documentation")
                        .version("1.0.0")
                        .description("API documentation for QuizApp"));
    }
}

//http://localhost:8080/swagger-ui/index.html#/