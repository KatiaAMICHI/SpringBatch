package com.jump;

import com.jump.queue.CustomSimpleJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableAsync
public class JobBatchLauncher {
    static final private String JOB_NAME1 = "jobA";
    static final private String JOB_NAME2 = "job";
    static final private String JOB_NAME3 = "jobC";
    static final private String JOB_NAME0 = "job0";
    static final private String TIME = "40 49 15 * * ?";

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier(JOB_NAME1)
    private Job job1;

    @Autowired
    @Qualifier(JOB_NAME2)
    private Job job2;

    @Autowired
    @Qualifier(JOB_NAME3)
    private Job job3;

    @Autowired
    @Qualifier(JOB_NAME0)
    private Job job0;

    @Async
    //@Scheduled(fixedRate = 2000)
    //@Scheduled(cron = TIME)
    public void run0() throws Exception {
        log.info("[Job] ....... run0");
        runJobB(this.job0);
    }

    @Async
    //@Scheduled(fixedRate = 2000)
    //@Scheduled(cron = TIME)
    public void run() throws Exception {
        log.info("[Job] ....... run1");
        runJobB(this.job1);
    }

    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public void run1() throws Exception {
        //Thread.sleep(5000);
        log.info("[Job2] ....... run1");
        runJobB(this.job2);
    }

    //@Async
    //@Scheduled(fixedRate = 3000)
    public void run3() throws Exception {
        log.info("[Job3] ....... run1");
        runJobB(this.job3);
    }

    private void runJobB(Job parJob) throws Exception {
        log.info("[Job] running ...........");

        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("value", new JobParameter("label_test_7"));
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        final CustomSimpleJobLauncher customSimpleJobLauncher = new CustomSimpleJobLauncher(jdbcOperations);
        if(customSimpleJobLauncher.canRunJob(parJob, jobParameters)) {
            try {
                jobLauncher.run(parJob, jobParameters);
            } catch (Exception ex) {
                log.error("[RUN JOB] : " + ex.getMessage());
            }
        }
    }
}