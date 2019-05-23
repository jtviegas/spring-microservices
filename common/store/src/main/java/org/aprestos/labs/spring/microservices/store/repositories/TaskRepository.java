package org.aprestos.labs.spring.microservices.store.repositories;

import org.aprestos.labs.spring.microservices.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task, String> {

}
