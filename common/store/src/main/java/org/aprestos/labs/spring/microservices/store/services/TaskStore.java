package org.aprestos.labs.spring.microservices.store.services;

import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.store.exceptions.MissingEntityException;

import java.util.List;
import java.util.Optional;

public interface TaskStore {
    List<Task> get();

    Task post(Task obj);

    Optional<Task> get(String id);

    void del(String id) throws MissingEntityException;
}
