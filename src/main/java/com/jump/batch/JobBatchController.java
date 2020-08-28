package com.jump.batch;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/job")
@Tag(name = "Job", description = "description jobs")
@Slf4j
public class JobBatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired @Qualifier("jobC")
    private Job job;

    @GetMapping("/load")
    public BatchStatus Load(@RequestParam("value") String parValue) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Map<String, JobParameter> confMap = new HashMap<>();

        confMap.put("value", new JobParameter(parValue));
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            JobExecution locJobExecution = jobLauncher.run(job, jobParameters);
            return locJobExecution.getStatus();
        }catch (Exception ex){
            log.error("job1 : " + ex.getMessage());
        }

        return BatchStatus.UNKNOWN;
    }

}
