package org.aprestos.labs.spring.microservices.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import org.aprestos.labs.spring.microservices.model.dto.Item;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Solution;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest {

	@Autowired
	private com.fasterxml.jackson.databind.ObjectMapper jsonMapper;

	private final String ENDPOINT_PROBLEM = "http://localhost:7710/api/problem";
	private final String ENDPOINT_TASK = "http://localhost:7710/api/task/{task-id}";
	private final String ENDPOINT_TASKS = "http://localhost:7710/api/task";


	@Before
	public void init(){
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
	}

	@Test
	public void test_simple_problem() throws Exception {

		Problem expected = new Problem(12, new Item[]{new Item(2,6), new Item(8,6), new Item(2,6), new Item(2,6)});
		HttpResponse<String> response = Unirest.post(ENDPOINT_PROBLEM)
				.header("accept", "text/plain")
				.header("Content-Type", "application/json")
				.body(expected).asString();

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
		String id = response.getBody();
		Assert.assertNotNull(id);
		Thread.sleep(10000);

		HttpResponse<Task> response2 = Unirest.get(ENDPOINT_TASK)
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.routeParam("task-id", id).asObject(Task.class);
		Assert.assertNotNull(response2.getBody().getSolution());
		Solution solution = response2.getBody().getSolution();
		Assert.assertEquals(2, solution.getItems().length);
		Assert.assertEquals(10, solution.getValue().intValue());

		HttpResponse<Task[]> response3 = Unirest.get(ENDPOINT_TASKS)
				.header("accept", "application/json")
				.header("Content-Type", "application/json").asObject(Task[].class);
		Assert.assertEquals(HttpStatus.OK.value(), response3.getStatus());
		Assert.assertEquals(1, response3.getBody().length);

	}


}
