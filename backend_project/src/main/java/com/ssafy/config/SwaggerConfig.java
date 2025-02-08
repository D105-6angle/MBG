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
        // 환경(개발/운영)에 따른 서버 URL 설정
        Server server = ("dev".equals(activeProfile))
                ? new Server().url("http://localhost:8080").description("Development Server")
                : new Server().url("https://i12d106.p.ssafy.io").description("Production Server");

        // Swagger UI에 표시될 API 문서 기본 정보 설정
        Info info = new Info()
                .title("Munbangu API Documentation")
                .version("1.0.0")
                .description("Munbangu");

        // JWT 인증 방식 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)     // HTTP 인증 방식 사용
                .scheme("bearer")                   // Bearer(https://docs.tosspayments.com/resources/glossary/bearer-auth) 인증 방식 사용
                .bearerFormat("JWT")                // JWT 형식 사용
                .in(SecurityScheme.In.HEADER)       // 헤더에 토큰을 담아 전송할 건데,
                .name("Authorization");             // 그 헤더 이름은 "Authorization"

        // 모든 API 요청에 인증이 필요함을 명시
        // Swagger UI에서 자물쇠 아이콘으로 표시됨
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");  // 위에서 설정한 내용을 모든 API에 적용

        // 최종 OpenAPI 설정 반환
        return new OpenAPI()
                .servers(List.of(server))               // 서버 정보 설정
                .info(info)                             // API 문서 기본 설정
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))  // 인증 방식 설정
                .addSecurityItem(securityRequirement);  // 인증 요구사항 적용
    }
}
