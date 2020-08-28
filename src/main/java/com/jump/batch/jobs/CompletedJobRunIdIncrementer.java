package com.jump.batch.jobs;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;

@AllArgsConstructor
public class CompletedJobRunIdIncrementer extends RunIdIncrementer {
    private final JobRepository jobRepository;
    private final String jobName;

    @Override
    public JobParameters getNext(JobParameters parameters) {
        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, parameters);
        return lastJobExecution == null || lastJobExecution.getStatus() == BatchStatus.COMPLETED ? super.getNext(parameters) : parameters;
    }

}