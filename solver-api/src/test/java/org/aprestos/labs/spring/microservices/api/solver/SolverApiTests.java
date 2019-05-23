package org.aprestos.labs.spring.microservices.api.solver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.dto.TaskStatus;
import org.aprestos.labs.apis.springboot2.statemanager.StateManager;
import org.aprestos.labs.spring.microservices.testtools.UtilsModel;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = Boot.class)
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class SolverApiTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper jsonMapper;

	@MockBean
	private StateManager<String, Task, TaskStatus> stateManager;

	private final String ENDPOINT = "/solver";

	@Test
	public void test_001_post() throws Exception {

		Problem expected = UtilsModel.createProblem();

		MvcResult response = this.mockMvc.perform(post(ENDPOINT)
				.contentType("application/json")
				.content(jsonMapper.writeValueAsString(expected)))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		String id = response.getResponse().getContentAsString();
		Assert.assertNotNull(id);
	}

	@Test
	public void test_002_shouldAcceptOnlyValidProblems() throws Exception {
		String wrongJson = "{\"prop\": 1234}";
		mockMvc.perform(post(ENDPOINT).contentType("application/json").content(wrongJson))
				.andExpect(status().is(422));
	}




}
