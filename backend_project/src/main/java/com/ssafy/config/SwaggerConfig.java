package com.ssafy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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

    // TODO: 로컬은 dev, 서버는 prod를 읽어와야 함
    @Value("${spring.profiles.active:dev}")  // application.properties의 값을 읽어옴
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {
        Server server = ("dev".equals(activeProfile))
                ? new Server().url("http://localhost:8080")
                : new Server().url("https://i12d106.p.ssafy.io");

        Info info = new Info()
                .title("Munbangu API Documentation")
                .version("1.0.0")
                .description("Munbangu");

        return new OpenAPI()
                .servers(List.of(server))
                .info(info);
    }
}
