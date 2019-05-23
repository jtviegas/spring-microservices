package org.aprestos.labs.spring.microservices.store.services;

import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.transform.Task2Dto;
import org.aprestos.labs.spring.microservices.model.transform.Task2entity;
import org.aprestos.labs.spring.microservices.store.exceptions.MissingEntityException;
import org.aprestos.labs.spring.microservices.store.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskStoreImpl implements TaskStore {

  @Autowired
  private TaskRepository taskRepository;

  private final Task2Dto mapper2dto = new Task2Dto();
  private final Task2entity mapper2entity = new Task2entity();
  
  @Override
  public List<Task> get() {
    log.trace("[get|in]");
    List<Task> result = new ArrayList<Task>();
    Iterator<org.aprestos.labs.spring.microservices.model.entities.Task> iterator = taskRepository.findAll().iterator();
    while(iterator.hasNext())
      result.add( mapper2dto.apply(iterator.next()) );

    log.trace("[get|out]");
    return result;
  }


  @Override
  public Task post(Task obj) {
    log.trace("[post|in]({})", obj);
    Task result = null;
    org.aprestos.labs.spring.microservices.model.entities.Task task = mapper2entity.apply(obj);
    result = mapper2dto.apply(taskRepository.save(task));
    log.trace("[post|out] {}", result);
    return result;
  }

  @Override
  public Optional<Task> get(String id) {
    log.trace("[get|in] ({})", id);
    Optional<Task> result = null;

    Optional<org.aprestos.labs.spring.microservices.model.entities.Task> data = taskRepository.findById(id);
    if(data.isPresent()) 
      result = Optional.of( mapper2dto.apply(data.get()) );
    else
      result = Optional.empty();

    log.trace("[get|out] {}", result);
    return result;
  }

  @Override
  public void del(String ident) throws MissingEntityException {
    log.trace("[del|in] ({})", ident);
    try {
      taskRepository.deleteById(ident);
    } catch (EmptyResultDataAccessException erdae) {
      log.error("[delTask] item not found", erdae);
      throw new MissingEntityException(erdae);
    }
    log.trace("[del|out]");
  }

}
