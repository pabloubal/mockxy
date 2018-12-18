package io.github.pabloubal.mockxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties
@ComponentScan("io.github.pabloubal.mockxy.*")
public class MockxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockxyApplication.class, args);
	}

}

