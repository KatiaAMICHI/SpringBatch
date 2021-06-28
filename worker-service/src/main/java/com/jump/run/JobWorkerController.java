package com.jump.run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * class qui permet de lancer des jobs a partir d'un appel API
 */
@RestController
@Slf4j
@Profile("master")
public class JobWorkerController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired @Qualifier("job_partition_master")
    private Job job;

    @RequestMapping("/startjobs")
    public String startJobs(@RequestParam(value = "label") final String parLabel, @RequestParam(value = "index") final int parIndex) {
        log.info("runnig job with label value : " + parLabel);
        for (int locIndex = 0; locIndex<parIndex; locIndex++) {
            //Thread.sleep(2000);
            final int locNewIndex = locIndex;
            new Thread(() -> runJobB(this.job, parLabel + locNewIndex)).start();
        }
        return "status";
    }

    @RequestMapping("/startjob")
    public String startJob(@RequestParam(value = "label") final String parLabel) {
        log.info("runnig job with label value : " + parLabel);
        runJobB(this.job, parLabel);
        return "status";
    }

    public Long runJobB(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");

        final JobParameters locParamJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        try {
            return jobLauncher.run(parJob, locParamJobParameters).getId();
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
        return null;
    }

}