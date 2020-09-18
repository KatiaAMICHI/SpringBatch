package com.jump;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
@Tag(name = "Job", description = "description jobs")
@Slf4j
public class JobBatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @PostMapping("/load")
    public BatchStatus Load(@RequestParam("job") Job job, @RequestParam("params") JobParameters jobParameters) {
        try {
            // JobExecution locJobExecution = jobLauncher.run(job, jobParameters);
            // return locJobExecution.getStatus();
        } catch (Exception ex) {
            log.error("job1 : " + ex.getMessage());
        }

        return BatchStatus.UNKNOWN;
    }

}
