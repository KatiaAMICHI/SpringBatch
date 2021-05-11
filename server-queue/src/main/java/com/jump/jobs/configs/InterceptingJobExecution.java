package com.jump.jobs.configs;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Setter
public class InterceptingJobExecution implements JobExecutionListener {
    private final JobRepository jobRepository;
    private final JdbcOperations jdbcOperations;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    @SneakyThrows
    @Override
    public void beforeJob(final JobExecution parJobExecution) {
        final Long locJobExecutionId = startJob(parJobExecution);
        log.info("[Before Job] Intercepting Job Excution - jobExecutionId id : {} ", locJobExecutionId);
    }

    @SneakyThrows
    @Override
    public void afterJob(final JobExecution parJobExecution) {
    }

    @SneakyThrows
    public Long startJob(final JobExecution parJobExecution) {
        // get the jobexecution to restart
        final CustomJdbcJobExecutionDao locCustomJdbcJobExecutionDao = new CustomJdbcJobExecutionDao(jdbcOperations);
        final List<JobExecution> locLatestRunningJobs = locCustomJdbcJobExecutionDao.getRunningJobExecutionsWithParams(parJobExecution.getId(), parJobExecution.getJobInstance().getJobName(), parJobExecution.getJobParameters());

        JobExecution locJobExecution;

        final JobExecutionService locJobExecutionService = new JobExecutionService(jobRepository, jobRegistry, jobLauncher);
        if (locLatestRunningJobs.isEmpty()) {
            // do nothing
            return null;
        }

        if (null != (locJobExecution = locJobExecutionService.containtCompletedStatus(locLatestRunningJobs))) {
            // job with the same parameter is running, we stop the new job that we are trying to execute
            final Long locJobExecutionID = locJobExecution.getId();
            log.info("Job with id %d already running with same parameter {}", locJobExecutionID);
            stopJpb(parJobExecution);
            return locJobExecutionID;
        } else if ( null != (locJobExecution = locJobExecutionService.getFirstJobExecutioin(locLatestRunningJobs, parJobExecution.getJobParameters()))) {
            log.info("job with id %d must be executed in first : {} ", locJobExecution.getId());
            // get the oldest running job and stop the previous one
            stopJpb(parJobExecution);
            // restart locJobExecution
            return locJobExecutionService.restartJob(locJobExecution);
        }

        return null;
    }

    private void stopJpb(final JobExecution parJobExecution) {
        synchronized (parJobExecution.getId()) {
            parJobExecution.stop();
            jobRepository.update(parJobExecution);
        }
    }

}