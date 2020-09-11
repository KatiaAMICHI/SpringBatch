package com.jump.batch.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableAsync
public class JobBatchLauncher {
    static final private String JOB_NAME1 = "jobA";
    static final private String JOB_NAME2 = "job";
    static final private String JOB_NAME3 = "jobC";
    static final private String TIME = "00 16 11 * * ?";

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired @Qualifier(JOB_NAME1)
    private Job job1;

    @Autowired @Qualifier(JOB_NAME2)
    private Job job2;

    @Autowired @Qualifier(JOB_NAME3)
    private Job job3;

    @Async
    @Scheduled(fixedRate = 100000000)
    //@Scheduled(cron = TIME)
    public void run() throws Exception {
        log.info("[Job1] ....... run1");
        runJobB(this.job1);
    }

    //@Scheduled(fixedRate = 7000)
    @Scheduled(fixedRate = 100000000)
    @Scheduled(cron = TIME)
    public void run1() throws Exception {
        Thread.sleep(5000);
        log.info("[Job2] ....... run1");
        runJobB(this.job2);
    }

    @Async
    @Scheduled(fixedRate = 100000000)
    public void run3() throws Exception {
        log.info("[Job3] ....... run1");
        Thread.sleep(11000);
        runJobB(this.job3);
    }

    private void runJobB(Job parJob) throws Exception {
        log.info("[Job] running ...........");

        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("value", new JobParameter("label_test5"));
        JobParameters jobParameters = new JobParameters(confMap);

        CustomSimpleJobLauncher customSimpleJobLauncher = new CustomSimpleJobLauncher(jdbcOperations);
        if(customSimpleJobLauncher.canRunJob(parJob, jobParameters)) {
            try {
                jobLauncher.run(parJob, jobParameters);
            } catch (Exception ex) {
                log.error("[RUN JOB] : " + ex.getMessage());
            }
        }
    }
}