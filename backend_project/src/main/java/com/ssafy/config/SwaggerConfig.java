package com.ssafy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
    Local Swagger URL: http://localhost:8080/swagger-ui/index.html
    EC2 Server Swagger URL: http://i12d106.p.ssafy.io/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server httpServer = new Server().url("https://i12d106.p.ssafy.io");     // HTTPS만 사용하도록 설정
//        Server httpServer = new Server().url("http://localhost:8080");     // HTTPS만 사용하도록 설정
        Info info = new Info().title("Munbangu API Documentation").version("1.0.0").description("Munbangu");

        // TODO: 소셜 로그인 토큰 설정 추가하는 부분
        return new OpenAPI().servers(List.of(httpServer)).info(info);
    }
}
