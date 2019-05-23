package org.aprestos.labs.spring.microservices.solver.k;

import org.aprestos.labs.apis.springboot2.statemanager.StateManagerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ComponentScan(basePackageClasses = {StateManagerConfig.class, Solver.class})
@EnableAutoConfiguration
@Configuration
public class Config {


	@Value("${org.aprestos.labs.apis.springboot2.solver.blocking-coefficient}")
	private double blockingCoefficient;

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

		if (1 < blockingCoefficient || 0 > blockingCoefficient)
			throw new RuntimeException("blockingCoefficient should be in between [0.0,1.0]");

		// one decimal only
		blockingCoefficient = ((int) (blockingCoefficient / 0.1)) * 0.1;

		int corePoolSize = Runtime.getRuntime().availableProcessors();
		int maxPoolSize = (int) (blockingCoefficient == 1.0 ? corePoolSize * 10
				: corePoolSize / (1 - blockingCoefficient));

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix("kworker-pool-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();
		executor.setDaemon(true);
		return executor;
	}

}
