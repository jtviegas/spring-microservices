package org.aprestos.labs.spring.microservices.batch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= BatchConfig.class)
public class SpringbatchApplicationTests {

    private static final String MSG="{\n" +
            "  \"ident\": \"123456789\"\n" +
            "  , \"timestamp\": 123456789\n" +
            "  , \"text\": \"this is text\"\n" +
            "}";
    private static final String TEST_FILE = String.format("%s%s%s", System.getProperty("java.io.tmpdir"),
            System.getProperty("file.separator"), SpringbatchApplicationTests.class.getSimpleName());

    @Autowired
    private Job job;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    JobLauncher jobLauncher;

    @Before
    public void init() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(TEST_FILE),false);
        fos.write(MSG.getBytes());
        fos.close();
    }

    @Test
    public void testJob() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder().addString("file", TEST_FILE).toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job,jobParameters);
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }

}
