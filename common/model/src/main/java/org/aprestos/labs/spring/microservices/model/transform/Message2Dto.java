package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.aprestos.labs.spring.microservices.model.entities.Message;

import java.util.function.Function;

public class Message2Dto implements Function<Message, MessageDto> {
    @Override
    public MessageDto apply(Message msg) {
        MessageDto result = new MessageDto();
        result.setIdent(msg.getIdent());
        result.setTimestamp(msg.getTimestamp());
        result.setText(msg.getText());
        return result;
    }
}
