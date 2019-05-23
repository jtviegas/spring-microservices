package org.aprestos.labs.spring.microservices.api.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude={JacksonAutoConfiguration.class} )
@EnableConfigurationProperties
@Import({org.aprestos.labs.spring.microservices.store.Boot.class})
public class Boot {

	public static void main(String[] args) {
		SpringApplication.run(Boot.class, args);
	}

	@Bean
	public ObjectMapper jsonMapper() {
		ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());

		return mapper;
	}

}
