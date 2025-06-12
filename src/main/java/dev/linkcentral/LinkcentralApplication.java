package dev.linkcentral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LinkcentralApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkcentralApplication.class, args);
	}
}
