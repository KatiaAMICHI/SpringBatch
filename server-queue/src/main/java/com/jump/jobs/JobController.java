package com.jump.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class JobController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired @Qualifier("jobasset")
    private Job job;

    @RequestMapping("/startjobs")
    public String startJobs(@RequestParam(value = "label") final String parLabel, @RequestParam(value = "index") final int parIndex) {
        log.info("runnig job with label value : " + parLabel);
        for (int locIndex = 0; locIndex<parIndex; locIndex++) {
            //Thread.sleep(2000);
            runJobB(this.job, parLabel + locIndex);
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