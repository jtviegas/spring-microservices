package org.aprestos.labs.spring.microservices.store.repositories;

import org.aprestos.labs.spring.microservices.model.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, String> {

}
