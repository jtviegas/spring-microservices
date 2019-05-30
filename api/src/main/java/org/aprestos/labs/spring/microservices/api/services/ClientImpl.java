package org.aprestos.labs.spring.microservices.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
public class ClientImpl implements Client {

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper jsonMapper;

    @Value("${org.aprestos.labs.apis.springboot2.api.uris.solver.scheme}")
    private String solverScheme;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.solver.host}")
    private String solverHost;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.solver.port}")
    private String solverPort;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.solver.path}")
    private String solverPath;

    @Value("${org.aprestos.labs.apis.springboot2.api.uris.store.scheme}")
    private String storeScheme;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.store.host}")
    private String storeHost;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.store.port}")
    private String storePort;
    @Value("${org.aprestos.labs.apis.springboot2.api.uris.store.path}")
    private String storePath;

    private String getSolverEndpoint(){
        return format("%s://%s:%s%s", solverScheme, solverHost, solverPort, solverPath);
    }

    private String getStoreEndpoint(){
        return format("%s://%s:%s%s", storeScheme, storeHost, storePort, storePath);
    }

    public ClientImpl(){
        log.trace("[ClientImpl|in]");
        Unirest.config().setObjectMapper(new ObjectMapper() {
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jsonMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            public String writeValue(Object value) {
                try {
                    return jsonMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.trace("[ClientImpl|out]");
    }



    @Override
    public String post(Problem problem) throws Exception {
        log.trace("[post|in] ({})", problem);
        String result = Unirest.post(getSolverEndpoint())
                .header("accept", "text/plain")
                .header("Content-Type", "application/json")
                .body(problem).asString().getBody();
        log.trace("[post|out] => {}", result);
        return result;
    }

    @Override
    public Optional<Task> get(String taskId) throws Exception {
        log.trace("[get|in] ({})", taskId);
        Optional<Task> result = null;

        HttpResponse<Task> response = Unirest.get(format("%s/{task-id}", getStoreEndpoint()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .routeParam("task-id", taskId).asObject(Task.class);
        if( HttpStatus.OK.value() == response.getStatus() )
            result = Optional.of(response.getBody());
        else if ( HttpStatus.NOT_FOUND.value() == response.getStatus() )
            result = Optional.empty();
        else {
            log.warn("[get] could not find a task for id {} [{}]", taskId, response);
            throw new Exception(format("could not find a task for id %s", taskId));
        }
        log.trace("[get|out] => {}", result);
        return result;
    }

    @Override
    public List<Task> get() throws Exception {
        log.trace("[get|in]");
        List<Task> result = null;

        HttpResponse<Task[]> response = Unirest.get(getStoreEndpoint())
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .asObject(Task[].class);

        if( HttpStatus.OK.value() == response.getStatus() )
            result = Arrays.asList(response.getBody());
        else {
            log.warn("[get] could not find tasks [{}]", response);
            throw new Exception("could not find tasks");
        }

        log.trace("[get|out] => {}", result);
        return result;
    }

}
