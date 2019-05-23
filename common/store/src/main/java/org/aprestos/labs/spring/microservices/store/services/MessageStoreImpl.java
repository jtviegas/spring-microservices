package org.aprestos.labs.spring.microservices.store.services;

import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.aprestos.labs.spring.microservices.model.entities.Message;
import org.aprestos.labs.spring.microservices.model.transform.Message2Dto;
import org.aprestos.labs.spring.microservices.model.transform.Message2Entity;
import org.aprestos.labs.spring.microservices.store.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class MessageStoreImpl implements MessageStore {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageStoreImpl.class);

  @Autowired
  private MessageRepository repository;
  private final Message2Dto message2dto = new Message2Dto();
  private final Message2Entity message2entity = new Message2Entity();
  
  public List<MessageDto> getMessages() {
    LOGGER.trace("[getMessages|in]");
    List<MessageDto> result = new ArrayList<MessageDto>();
    Iterator<Message> iterator = repository.findAll().iterator();
    while(iterator.hasNext())
      result.add( message2dto.apply(iterator.next()) );

    LOGGER.trace("[getMessages|out]");
    return result;
  }

  @Override
  public String postMessage(MessageDto msg) {
    LOGGER.trace("[postMessage|in]({})", msg);
    String result = repository.save(message2entity.apply(msg)).getIdent();
    LOGGER.trace("[postMessage|out] {}", result);
    return result;
  }

  @Override
  public Optional<MessageDto> getMessage(String ident) {
    LOGGER.trace("[getMessage|in] ({})", ident);
    Optional<MessageDto> result = null;

    Optional<Message> data = repository.findById(ident);
    if(data.isPresent()) 
      result = Optional.of( message2dto.apply(data.get()) );
    else
      result = Optional.empty();

    LOGGER.trace("[getMessage|out] {}", result);
    return result;
  }

  @Override
  public void delMessage(String ident) {
    LOGGER.trace("[delMessage|in] ({})", ident);
    repository.deleteById(ident);
    LOGGER.trace("[delMessage|out]");
  }

}
