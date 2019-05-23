package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.aprestos.labs.spring.microservices.model.entities.Message;

import java.util.function.Function;

public class Message2Entity implements Function<MessageDto, Message> {
    @Override
    public Message apply(MessageDto dto) {
        Message result = new Message();
        result.setIdent(dto.getIdent());
        result.setTimestamp(dto.getTimestamp());
        result.setText(dto.getText());
        return result;
    }
}
