package com.jump.batch.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@EnableAsync
public class JobBatchLauncher {
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired @Qualifier("jobC")
    private Job job;

    //@Async
    //@Scheduled(fixedRate = 7000)
    public void run() throws Exception {
        log.info("[Job] ....... run1");
        runJobB();
    }

    //@Async
    //@Scheduled(fixedRate = 7000)
    public void run1() throws Exception {
        log.info("[Job] ....... run1");
        runJobB();
    }

    private void runJobB() throws Exception {

        log.info("[Job] running ...........");

        Map<String, JobParameter> confMap = new HashMap<>();

        confMap.put("value", new JobParameter("label_test"));
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        // récupérer les param du dernier job qui a été lancé
        // JobParameters jobParams = job.getJobParametersIncrementer().getNext(jobParameters);
        List<JobExecution> jobExecutionList = new ArrayList<>(jobExplorer.findRunningJobExecutions("jobC"));

        if (jobExecutionList.size() < 1) {
            try {
                jobLauncher.run(job, jobParameters);
            } catch (Exception ex) {
                log.error("job1 : " + ex.getMessage());
            }
        }
    }
}