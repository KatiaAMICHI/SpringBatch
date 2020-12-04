package com.jump.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JobsLauncher {

    private final static String MASTER_JOB_TEST = "remotePartitioningJob";

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(MASTER_JOB_TEST)
    private Job job;

    @Scheduled(fixedRate = 3000)
    private void run() {
        log.info("[Job] running ...........");

        final Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("value", new JobParameter("label_test_master"));
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        final JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(job, jobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }
}
