package org.aprestos.labs.spring.microservices.store.services;


import org.aprestos.labs.spring.microservices.model.dto.MessageDto;

import java.util.List;
import java.util.Optional;

public interface MessageStore {
  List<MessageDto> getMessages();
  String postMessage(MessageDto msg);
  Optional<MessageDto> getMessage(String ident);
  void delMessage(String ident);

}
