package org.aprestos.labs.spring.microservices.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@Slf4j
@Configuration
public class AppConfig {


    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    public void doJob() throws Exception
    {
        log.info("[doJob|in] job started");

        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = jobLauncher.run(job, param);

        log.info("[doJob|out] status: {}", execution.getStatus());
    }



}
