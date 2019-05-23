package org.aprestos.labs.spring.microservices.solver.k;

import org.aprestos.labs.apis.springboot2.statemanager.StateManager;
import org.aprestos.labs.spring.microservices.model.dto.Item;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.dto.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class Tests {

	@Autowired
	private Solver solver;

	@MockBean
	private StateManager<String, Task, TaskStatus> stateManager;

	@Test
	public void test_001_shouldAcceptProblem() throws Exception {

		Problem problem = new Problem(15,
				new Item[]{new Item(4, 12), new Item(2, 1),
						new Item(2, 2), new Item(1, 1), new Item(10, 4)});

		Task task = new Task(problem);

		doAnswer((o) -> null ).when(stateManager).notify(task);

		String id = solver.solve(problem);

		Thread.sleep(5000);

	}


}
