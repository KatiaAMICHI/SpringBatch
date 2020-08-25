package com.jump.batch;

import com.jump.batch.obj.AssetKVRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/job")
@Tag(name = "Job", description = "description jobs")
public class JobBatchController {


    @Autowired
    private AssetKVRepository assetKVRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @GetMapping("/getJob")
    public BatchStatus load() throws Exception {
        Map<String, JobParameter> jobParameterMap = new HashMap<String, JobParameter>();
        jobParameterMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(jobParameterMap);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        while (jobExecution.isRunning()) {
            System.out.println("..... runing ......");
        }

        return jobExecution.getStatus();
    }

    @GetMapping("/load")
    public BatchStatus Load(@RequestParam("value") String parValue) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addString("value", parValue).toJobParameters());
        final BatchStatus status = jobExecution.getStatus();

        return status;
    }

}
