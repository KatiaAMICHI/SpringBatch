package com.jump;

import com.jump.objects.JobEvent;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableBinding(Source.class)
@Slf4j
public class StatusController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired @Qualifier("jobasset")
    private Job job;

    @Autowired @Qualifier("projob")
    private Job projob;

    @Autowired
    private Source sources;

    @RequestMapping("/test")
    public String testOk() {
        return "test OK";
    }

    @RequestMapping("/jobevent")
    public String jobEventTest() {
        final JobEvent event = new JobEvent();

        Message<JobEvent> msg = new GenericMessage<>(event);
        log.info("msg to send : " + msg);
        sources.output().send(msg);

        // return rest call
        return "JobEvent";
    }

    @RequestMapping("/startjob1")
    public String startJob() {
        Message<String> msg = new GenericMessage<>("payload");
        log.info("runnig job " + msg);
        //sources.output().send(msg);
        runJobB(this.job);
        // return rest call
        return "status";
    }

    @RequestMapping("/projob")
    public String runprojob() {
        log.info("runnig job : projob");
        //sources.output().send(msg);
        runJobB(this.projob);
        // return rest call
        return "status";
    }

    private void runJobB(Job parJob) {
        log.info("[Job] running ...........");
        // confMap :  les params dans la requete api
        JobParameters paramJobParameters = new JobParametersBuilder()
                .addParameter("value", new JobParameter("label_test_7"))
                .addParameter("time", new JobParameter(System.currentTimeMillis()))
                .toJobParameters();

        try {
            jobLauncher.run(parJob, paramJobParameters);
        } catch (Exception ex) {
            log.error("[RUN JOB] : " + ex.getMessage());
        }
    }

}