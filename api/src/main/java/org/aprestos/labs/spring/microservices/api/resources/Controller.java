package org.aprestos.labs.spring.microservices.api.resources;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.api.exceptions.ApiException;
import org.aprestos.labs.spring.microservices.api.services.Client;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(value = "/api")
@Api(tags = { "main api" }, value = "API root")
@ApiResponses(value = { @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid status value", response = void.class),
    @io.swagger.annotations.ApiResponse(code = 500, message = "Internal server error", response = void.class) })
public class Controller {

  @Autowired
  private Client client;


  @RequestMapping(value = "/problem", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
  @ApiOperation(value = "Used to post a problem", response = String.class, produces = "text/plain", consumes = "application/json")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "successful operation", response = String.class) })
  public ResponseEntity<String> postProblem(@RequestBody @Valid Problem problem) throws ApiException {
    log.trace("[postProblem|in] ({})", problem);
    String result = null;
    try {
      result = client.post(problem);
      MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
      header.add("Content-Type", "text/plain");
      return new ResponseEntity<>(result, header, HttpStatus.OK);
    }
    catch(Exception e){
      throw new ApiException(e);
    }
    finally {
      log.trace("[postProblem|out] => {}", result);
    }
  }

  @RequestMapping(value = "/task/{task-id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Used to get a task state", response = Task.class, produces = "application/json")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "successful operation", response = Task.class) })
  public ResponseEntity<Task> getTask(@Valid @NotNull @ApiParam(required = true) @PathVariable("task-id") String taskId) throws ApiException {
    log.trace("[getTask|in] ({})", taskId);
    ResponseEntity<Task> result = null;
    try {
      Optional<Task> response = client.get(taskId);
      if( response.isPresent() ){
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Content-Type", "application/json");
        result = new ResponseEntity<>(response.get(), header, HttpStatus.OK);
      }
      else{
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Content-Type", "application/json");
        result = new ResponseEntity<>( header, HttpStatus.NOT_FOUND);
      }
      return result;
    }
    catch(Exception e){
      throw new ApiException(e);
    }
    finally {
      log.trace("[getTask|out] => {}", result);
    }
  }

  @RequestMapping(value = "/task", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Used to get all tasks state", response = Task.class, responseContainer = "List", produces = "application/json")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "successful operation", response = Task.class, responseContainer = "List") })
  public ResponseEntity<List<Task>> getTasks() throws ApiException {
    log.trace("[getTasks|in]");
    List<Task> result = null;
    try {
      result = client.get();
      MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
      header.add("Content-Type", "application/json");
      return new ResponseEntity<>(result, header, HttpStatus.OK);
    }
    catch(Exception e){
      throw new ApiException(e);
    }
    finally {
      log.trace("[getTasks|out] => {}", result);
    }
  }


}
