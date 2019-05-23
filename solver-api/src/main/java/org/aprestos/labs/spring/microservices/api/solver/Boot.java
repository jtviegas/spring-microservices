package org.aprestos.labs.spring.microservices.api.solver;

import org.aprestos.labs.spring.microservices.solver.k.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties
@Import({Config.class})
public class Boot {

	public static void main(String[] args) {
		SpringApplication.run(Boot.class, args);
	}

}
