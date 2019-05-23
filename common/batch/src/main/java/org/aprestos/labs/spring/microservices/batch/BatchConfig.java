package org.aprestos.labs.spring.microservices.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.aprestos.labs.spring.microservices.model.dto.MessageDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableBatchProcessing
public class BatchConfig {

    private static final String INJECTED_STRING_AT_STEP_SCOPE = null;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    @StepScope
    public ItemReader<MessageDto> reader(@Value("#{jobParameters['file']}") String file){
        return new MessageReader(jsonMapper(), file);
    }

    @Bean
    public MessageItemProcessor processor(){
        return new MessageItemProcessor();
    }

    @Bean
    public ItemWriter<MessageDto> writer(){
        return new MessageWriter();
    }


    @Bean
    public ObjectMapper jsonMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        return mapper;
    }

    @Bean
    public Step step() {
        return steps.get("step").chunk(1)
                .reader(reader(INJECTED_STRING_AT_STEP_SCOPE))
                .processor((ItemProcessor)processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(Step step) {
        return jobs.get("job")
                .preventRestart()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("starting job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("finished job");
                    }
                })
                .validator(new DefaultJobParametersValidator())
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }

}
