package io.github.pabloubal.mockxy.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MockxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockxyApplication.class, args);
	}

}

