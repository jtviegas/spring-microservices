package org.aprestos.labs.spring.microservices.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.micrometer.core.instrument.MeterRegistry;

@EnableConfigurationProperties
@SpringBootApplication
public class Boot {

  @Autowired
  private ApplicationContext context;

  public static void main(String[] args) {
    SpringApplication.run(Boot.class, args);
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    final String hostname = null != context.getEnvironment().getProperty("HOSTNAME")
        ? context.getEnvironment().getProperty("HOSTNAME")
        : context.getEnvironment().getProperty("COMPUTERNAME");

    return registry -> registry.config().commonTags("host", null != hostname ? hostname : "localhost");
  }

  @Bean
  public ObjectMapper jsonMapper() {
    ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule());

    return mapper;
  }
}
