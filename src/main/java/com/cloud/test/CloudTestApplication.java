package com.cloud.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Start
 */
@SpringBootApplication
@MapperScan("com.cloud.test.*.mapper")
@EnableScheduling
public class CloudTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudTestApplication.class, args);
	}
}
