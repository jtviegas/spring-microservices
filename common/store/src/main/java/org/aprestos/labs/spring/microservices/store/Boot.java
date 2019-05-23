package org.aprestos.labs.spring.microservices.store;

import org.aprestos.labs.spring.microservices.model.entities.Task;
import org.aprestos.labs.spring.microservices.store.repositories.TaskRepository;
import org.aprestos.labs.spring.microservices.store.services.TaskStore;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackageClasses = TaskStore.class)
@EnableJpaRepositories(basePackageClasses = TaskRepository.class)
@EntityScan(basePackageClasses = Task.class)
@EnableTransactionManagement
@Configuration
public class Boot {

}
