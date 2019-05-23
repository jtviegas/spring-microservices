package org.aprestos.labs.spring.microservices.api.services;

import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;

import java.util.List;
import java.util.Optional;

public interface Client {
    String post(Problem problem) throws Exception;

    Optional<Task> get(String taskId) throws Exception;

    List<Task> get() throws Exception;
}
