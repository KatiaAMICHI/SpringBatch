package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class StatusController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private JobRepository jobRepository;

    @Autowired @Qualifier("jobasset")
    private Job job;

    @RequestMapping("/startjob1")
    public String startJob1(@RequestParam(value = "label") final String parLabel) {
        log.info("runnig job with label value : " + parLabel);
        runJobB(this.job, parLabel);
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

        //final InterceptingJobExecution interceptingJobExecution = new InterceptingJobExecution(jobExplorer, null);
        // final JobExecution locLatestRunningJob = interceptingJobExecution.getLatestRunningJob(parJob.getName(), paramJobParameters);

        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }

}