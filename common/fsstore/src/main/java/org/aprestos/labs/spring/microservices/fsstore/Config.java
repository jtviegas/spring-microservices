package org.aprestos.labs.spring.microservices.fsstore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Config {

    @Bean
    @Scope("singleton")
    public StorageService storageService(){
        return new FsStorageService();
    }

}
