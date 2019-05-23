package org.aprestos.labs.apis.springboot2.statemanager;

import org.aprestos.labs.apiclient.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ComponentScan(basePackageClasses = {RestClient.class})
@EnableAutoConfiguration
@Configuration
public class StateManagerConfig {


}
