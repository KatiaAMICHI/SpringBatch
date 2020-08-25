package com.jump.batch.jobs;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SingleInstanceListener implements JobExecutionListener {
    @Autowired
    private JobExplorer explorer;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        Set<JobExecution> executions = explorer.findRunningJobExecutions(jobName);
        if(executions.size() > 1) {
            jobExecution.stop();
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

}