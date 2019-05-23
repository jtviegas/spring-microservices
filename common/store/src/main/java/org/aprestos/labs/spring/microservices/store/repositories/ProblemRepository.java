package org.aprestos.labs.spring.microservices.store.repositories;

import org.aprestos.labs.spring.microservices.model.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProblemRepository extends JpaRepository<Problem, Long> {

}
