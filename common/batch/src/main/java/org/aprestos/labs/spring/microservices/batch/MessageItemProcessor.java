package org.aprestos.labs.spring.microservices.batch;

import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

@Slf4j
public class MessageItemProcessor implements ItemProcessor<MessageDto, MessageDto> {

    @Override
    public MessageDto process(MessageDto item) throws Exception {
        log.info("[process|in] ({})", item);
        item.setTimestamp(new Date().getTime());
        log.info("[process|out] => {}", item);
        return item;
    }
}
