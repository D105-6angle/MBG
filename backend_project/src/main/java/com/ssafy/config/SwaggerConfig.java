package com.ssafy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
    Local Swagger URL: http://localhost:8080/swagger-ui/index.html
    EC2 Server Swagger URL: http://i12d106.p.ssafy.io/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {
    @Value("${app.environment:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {
        Server server = ("dev".equals(activeProfile))
                ? new Server().url("http://localhost:8080").description("Development Server")
                : new Server().url("https://i12d106.p.ssafy.io").description("Production Server");

        Info info = new Info()
                .title("Munbangu API Documentation")
                .version("1.0.0")
                .description("Munbangu");

        // JWT SecurityScheme 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Security 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .servers(List.of(server))
                .info(info)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
