package org.aprestos.labs.spring.microservices.api.solver.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.apiutils.exceptions.ApiException;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.solver.k.Solver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/solver")
@Api(tags = { "solver api" }, value = "API root for a solver")
@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value"),
		@ApiResponse(code = 500, message = "Internal server error") })
public class Controller {

	@Autowired
	private Solver solver;

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Used to post a problem")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = String.class ) })
	public ResponseEntity<String> postProblem(@RequestBody @Valid Problem problem)
			throws ApiException {
		log.trace("[postProblem|in] problem: {}", problem);
		String result = null;
		try {
			result = solver.solve(problem);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("[postProblem]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.trace("[postProblem|out] => {}", result);
		}
	}



}
