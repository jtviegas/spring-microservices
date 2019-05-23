package org.aprestos.labs.apis.springboot2.statemanager;

import org.aprestos.labs.spring.microservices.model.dto.Item;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.dto.TaskStatus;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoundtripIntegrationTest {

	@Autowired
	private StateManager<String, Task, TaskStatus> manager;

	@Test
	public void test_dumbOne() throws Exception {

		MultiValueMap<String, String> header = new LinkedMultiValueMap();
		header.add("Content-Type", "application/json");
		header.add("Accept", "application/json");

		Problem problem = new Problem(15,
				new Item[]{new Item(4, 12), new Item(2, 1),
						new Item(2, 2), new Item(1, 1), new Item(10, 4)});

		Task task = new Task(problem);

		manager.notify(task);
		Assert.assertTrue(true);

	}

}
