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
        // 배포용 서버는 https만 허용 (Nginx 설정 때문)
        String activeProfile = System.getProperty("spring.profiles.active", "dev");
        Server server = ("dev".equals(activeProfile)) ? new Server().url("http://localhost:8080") : new Server().url("https://i12d106.p.ssafy.io");
        System.out.println("Server URL: " + server.getUrl());
        Info info = new Info().title("Munbangu API Documentation").version("1.0.0").description("Munbangu");

        // TODO: 소셜 로그인 토큰 설정 추가하는 부분
        return new OpenAPI().servers(List.of(server)).info(info);
    }
}
