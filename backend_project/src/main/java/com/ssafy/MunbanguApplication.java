package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.ssafy.model.mapper")
public class MunbanguApplication {
	public static void main(String[] args) {

		SpringApplication.run(MunbanguApplication.class, args);
	}
}
