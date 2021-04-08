package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class StatusController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired @Qualifier("jobasset")
    private Job job;

    @RequestMapping("/startjob1")
    public String startJob(@RequestParam(value = "parLabel") final String parLabel) {
        Message<String> msg = new GenericMessage<>("payload");
        log.info("runnig job " + msg);
        //sources.output().send(msg);
        runJobB(this.job, parLabel);
        // return rest call
        return "status";
    }

    private void runJobB(final Job parJob, final String parLabel) {
        log.info("[Job] running ...........");
        // confMap :  les params dans la requete api
        JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter(parLabel))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }

}