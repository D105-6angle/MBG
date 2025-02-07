package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ssafy.model.mapper")
public class MunbanguApplication {
	public static void main(String[] args) {

		SpringApplication.run(MunbanguApplication.class, args);
	}
}
