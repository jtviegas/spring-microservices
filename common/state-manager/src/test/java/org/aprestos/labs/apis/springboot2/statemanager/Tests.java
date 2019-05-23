package org.aprestos.labs.apis.springboot2.statemanager;

import org.aprestos.labs.apiclient.HeadersBuilder;
import org.aprestos.labs.apiclient.RestClient;
import org.aprestos.labs.spring.microservices.model.dto.Item;
import org.aprestos.labs.spring.microservices.model.dto.Problem;
import org.aprestos.labs.spring.microservices.model.dto.Task;
import org.aprestos.labs.spring.microservices.model.dto.TaskStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.lang.String.format;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {

	@Autowired
	private StateManager<String, Task, TaskStatus> manager;


	@MockBean
	private RestClient client;

	@Value("${org.aprestos.labs.apis.springboot2.uri.store.scheme}")
	private String storeScheme;
	@Value("${org.aprestos.labs.apis.springboot2.uri.store.host}")
	private String storeHost;
	@Value("${org.aprestos.labs.apis.springboot2.uri.store.port}")
	private String storePort;
	@Value("${org.aprestos.labs.apis.springboot2.uri.store.path}")
	private String storePath;

	private String getStoreEndpointSingular(){
		return format("%s://%s:%s%s/{task-id}", storeScheme, storeHost, storePort, storePath);
	}


	@Test
	public void test_dumbOne() throws Exception {

		MultiValueMap<String, String> header = new LinkedMultiValueMap();
		header.add("Content-Type", "application/json");
		header.add("Accept", "application/json");

		Problem problem = new Problem(15,
				new Item[]{new Item(4, 12), new Item(2, 1),
						new Item(2, 2), new Item(1, 1), new Item(10, 4)});

		Task task = new Task(problem);

		doAnswer((o) -> HeadersBuilder.create() ).when(client).getHeadersBuilder();
		doAnswer((o) -> null ).when(client).post(task, header, getStoreEndpointSingular());

		manager.notify(task);

	}

}
