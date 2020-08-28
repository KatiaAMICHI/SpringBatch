package com.jump.batch.jobs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class InterceptingJobExecution implements JobExecutionListener {
    static private List<JobInformation> jobInformations = new ArrayList<>();

    @Autowired
    private JobExplorer explorer;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobOperator jobOperator;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private ApplicationContext applicationContext;

    @SneakyThrows
    @Override
    public void beforeJob(JobExecution jobExecution) {
        synchronized (jobExecution) {
            log.info("Intercepting Job Excution - Before Job!");

            final String locJobName = jobExecution.getJobInstance().getJobName();
            final JobParameters locJobParameters = jobExecution.getJobParameters();

            List<JobExecution> jobExecutionList = new ArrayList<>(explorer.findRunningJobExecutions(locJobName));
            jobExecutionList.sort((o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime()));

            if (jobExecutionList.size() > 1) {
                jobInformations.add(new JobInformation(locJobName,  locJobParameters));
                jobExecution.setEndTime(new Date());
                jobExecution.setStatus(BatchStatus.FAILED);
                jobExecution.setExitStatus(ExitStatus.FAILED);
                jobExecution.stop();
                jobRepository.update(jobExecution);
                if (jobExecution != null) {
                    jobOperator.stop(jobExecution.getId());
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void afterJob(JobExecution jobExecution) {
        synchronized (jobExecution) {
            log.info("Intercepting Job Excution - After Job!");
            final String locJobName = jobExecution.getJobInstance().getJobName();
            final JobParameters locJobParameters = jobExecution.getJobParameters();

            Job job = applicationContext.getBean(locJobName, Job.class);
            jobLauncher.run(job, locJobParameters);

        }
    }
}