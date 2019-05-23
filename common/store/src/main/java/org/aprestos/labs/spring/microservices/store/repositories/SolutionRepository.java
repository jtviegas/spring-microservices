package org.aprestos.labs.spring.microservices.store.repositories;

import org.aprestos.labs.spring.microservices.model.entities.Solution;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SolutionRepository extends JpaRepository<Solution, Long> {

}
