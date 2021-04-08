package com.jump;

import com.jump.jobs.CustomSimpleJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
@EnableAsync
@EnableScheduling
public class JobBatchLauncher {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired @Qualifier("jobasset")
    private Job job;
    static final private String TIME = "10 30 15 * * ?"; // second, minute, hour, day of month, month, day(s) of week
    @Autowired
    private JdbcOperations jdbcOperations;

    //@Async
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public String startJob1() {
        log.info("runnig job " + "Label11111111");
        //sources.output().send(msg);
        runJobB(this.job, "Label1");
        // return rest call
        return "status";
    }

    //@Async
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = TIME)
    public String startJob2() {
        log.info("runnig job " + "Label222222");
        //sources.output().send(msg);
        runJobB(this.job, "Label2");
        // return rest call
        return "status";
    }

    private void runJobB(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");

        final CustomSimpleJobLauncher customSimpleJobLauncher = new CustomSimpleJobLauncher(jdbcOperations);

        // confMap :  les params dans la requete api
        JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        customSimpleJobLauncher.canRunJob(parJob, paramJobParameters);
        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }
}
