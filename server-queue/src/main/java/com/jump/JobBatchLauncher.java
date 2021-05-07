package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Configuration
@Slf4j
@EnableAsync
@EnableScheduling
public class JobBatchLauncher {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired @Qualifier("jobasset")
    private Job job;

    static final private String TIME = "50 58 17 * * ?"; // second, minute, hour, day of month, month, day(s) of week

    @Async
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public void startJob1() {
        log.info("runnig job with {] parameters " + "Label1");
        runJobB(this.job, "Label1");
    }

    @Async
    @Scheduled(cron = TIME)
    public void startJob2() {
        log.info("runnig job with {] parameters " + "Label2");
        runJobB(this.job, "Label2");
    }

    public JobExecution runJobB(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");

        // confMap :  les params dans la requete api
        final JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        try {
            return jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
        return null;
    }

}
