package org.aprestos.labs.spring.microservices.api.store.resources;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.apiutils.exceptions.ApiException;
import org.aprestos.labs.spring.microservices.apiutils.exceptions.NotFoundException;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.store.exceptions.MissingEntityException;
import org.aprestos.labs.spring.microservices.store.services.TaskStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/store/task")
@Api(tags = { "taskStore api" }, value = "API root for taskStore facade")
@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value"),
		@ApiResponse(code = 500, message = "Internal server error") })
public class Tasks {

	@Autowired
	private TaskStore taskStore;

	@RequestMapping(value = "/{task-id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Used to delete a specific task")
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation")
			, @ApiResponse(code = 404, message = "not found")})
	public ResponseEntity<Void> delTask(@Valid @NotNull @ApiParam(required = true) @PathVariable("task-id") String taskId)
			throws ApiException {
		log.trace("[delTask|in] ({})", taskId);
		try {
			taskStore.del(taskId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (MissingEntityException me) {
			log.error("[delTask] item not found", me);
			throw new ApiException(me, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("[delTask]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.trace("[delTask|out]");
		}
	}

	@RequestMapping(value = "/{task-id}", method = RequestMethod.GET)
	@ApiOperation(value = "Used to get a specific task")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Task.class)
	, @ApiResponse(code = 404, message = "not found")})
	public ResponseEntity<Task> getTask(@Valid @NotNull @ApiParam(required = true) @PathVariable("task-id") String taskId)
			throws ApiException {
		log.trace("[getTask|in] ({})", taskId);
		Task result = null;
		try {
			Optional<Task> optionalTask = taskStore.get(taskId);
			if(optionalTask.isPresent()) {
				result = optionalTask.get();
				return new ResponseEntity<>(result, HttpStatus.OK);
			}
			else
				throw new NotFoundException(String.format("not found taskId: %s", taskId), HttpStatus.NOT_FOUND);
		} catch (ApiException ae) {
			log.error("[getTask]", ae);
			throw ae;
		} catch (Exception e) {
			log.error("[getTask]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.trace("[getTask|out] => {}", result);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Used to post a task")
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = Task.class ) })
	public ResponseEntity<Task> postTask(@RequestBody @Valid Task task)
			throws ApiException {
		Task result = null;
		try {
			result = taskStore.post(task);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("[postTask]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.trace("[postTask|out] => {}", result);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Used to get all tasks")
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = Task.class, responseContainer = "List") })
	public ResponseEntity<List<Task>> getTasks()
			throws ApiException {
		log.trace("[getTasks|in]");
		List<Task> result = null;
		try {
			result = taskStore.get();
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("[getTasks]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.trace("[getTasks|out] => {}", result);
		}
	}

}
