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
    @Autowired
    private JdbcOperations jdbcOperations;
    @Autowired
    private JobExplorer jobExplorer;

    private int NB_JOB = 10;

    @Autowired @Qualifier("jobasset")
    private Job job;
    static final private String TIME = "50 58 17 * * ?"; // second, minute, hour, day of month, month, day(s) of week
    static final private String TIME_1 = "25 05 10 * * ?"; // second, minute, hour, day of month, month, day(s) of week

    //@Async
    @Scheduled(cron = TIME_1)
    public String startJob00() {
        log.info("1 -- runnig job00 with Label00");
        runJobB(this.job, "Label00");
        return "status";
    }

    //@Async
    @Scheduled(cron = TIME_1)
    public String startJob01() {
        log.info("2 -- runnig job01 with Label00");
        runJobB(this.job, "Label00");
        return "status";
    }


    @Async
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public String startJob1() {
        for(int i = 0; i <4; i++) {
            log.info("runnig job " + "Label1-" + i);
            runJobB(this.job, "Label1-" + i);
        }
        return "status";
    }

    @Async
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public String startJob2() {
        int i = 0;
        //for(i = 0; i <NB_JOB; i++) {
          //  log.info("runnig job " + "Label2-" + i);
            runJobB(this.job, "Label2-" + i);
        //}
        return "status";
    }

    @Async
    @Scheduled(cron = TIME)
    public String startJob3() {
        int i = 0;
        //for(int i = 0; i <NB_JOB; i++) {
           // log.info("runnig job " + "Label3-" + i);
            runJobB(this.job, "Label3-" + i);
        //}
        return "status";
    }

    @Async
    @Scheduled(cron = TIME)
    public String startJob4() {
        int i = 0;
        //for(i = 0; i <4; i++) {
          //  log.info("runnig job " + "Label4-" + i);
            runJobB(this.job, "Label4-" + i);
        //}
        return "status";
    }

    public void runJobB(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");

        // confMap :  les params dans la requete api
        JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .addParameter("nb_execution", new JobParameter((long) 1))
                .toJobParameters();

        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }


    public void runJobBWithQueue(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");

        // confMap :  les params dans la requete api
        JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        final JobExecution latestRunningJob = getLatestRunningJob(job, paramJobParameters);
        final JobParameters jobParametersToRun = latestRunningJob.getJobParameters();
        //customSimpleJobLauncher.canRunJob(parJob, paramJobParameters);
        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }

    private JobExecution getLatestRunningJob(final Job parJob, final JobParameters parParamJobParameters) {
        final Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(parJob.getName());
        JobExecution latestExecution = null;

        for (JobExecution execution : Optional.ofNullable(runningJobExecutions).orElse(Collections.emptySet())) {
            long minDiff = Long.MAX_VALUE;
            JobParameters params = execution.getJobParameters();

            log.warn("params - value {}", params.getString("value"));

            if ( ! parParamJobParameters.getString("value").equals(params.getString("value"))) {
                continue;
            }

            log.warn("create {}", execution.getCreateTime());
            log.warn("start {}", execution.getStartTime());

            long diff = parParamJobParameters.getLong("time") - execution.getCreateTime().getTime();
            if (diff < 0) {
                log.warn("Impossible, new job executed before old! Old JobExecution id: {}", execution.getJobId());
                continue;
            }
            if (diff < minDiff) {
                minDiff = diff;
                latestExecution = execution;
            }
        }
        return latestExecution;
    }
}
