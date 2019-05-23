package org.aprestos.labs.spring.microservices.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.springframework.batch.item.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MessageReader implements ItemReader<MessageDto>, ItemStream {

    private final ObjectMapper mapper;
    private final String file;
    private boolean toRead = true;

    public MessageReader(ObjectMapper jsonMapper, String file){
        this.mapper = jsonMapper;
        this.file = file;
    }

    @Override
    public MessageDto read() throws Exception {
        log.info("[read|in]");
        MessageDto result = null;
        if( toRead ){
            toRead = false;
            Path msg = Paths.get(file);
            if ( !msg.toFile().exists() || !msg.toFile().canRead()  )
                throw new RuntimeException("no message file to read");

            result = mapper.readValue(msg.toFile(), MessageDto.class);
        }
        log.info("[read|out] => {}", result);
        return result;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("[open|in] ({})", executionContext);
        log.info("[open|out]");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("[update|in] ({})", executionContext);
        log.info("[update|out]");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("[close|in]");
        new File(file).delete();
        log.info("[close|out]");
    }
}
