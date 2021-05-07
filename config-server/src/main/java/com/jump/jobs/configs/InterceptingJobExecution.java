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
        log.info("Intercepting Job Excution - Before Job!");
        final Long locJobExecutionId = startJob(parJobExecution);
        log.info("Intercepting Job Excution - jobExecutionId restart : " + locJobExecutionId);
    }

    @SneakyThrows
    @Override
    public void afterJob(final JobExecution parJobExecution) {
    }

    @SneakyThrows
    public Long startJob(final JobExecution parJobExecution) {
        // 1 . récuper le jobexecution a relancer
        final CustomJdbcJobExecutionDao customJdbcJobExecutionDao = new CustomJdbcJobExecutionDao(jdbcOperations);
        final List<JobExecution> locLatestRunningJobs = customJdbcJobExecutionDao.getRunningJobExecutionsWithParams(parJobExecution.getId(), parJobExecution.getJobInstance().getJobName(), parJobExecution.getJobParameters());

        JobExecution locJobExecutioin;

        final JobExecutionService locJobExecutionService = new JobExecutionService(jobRepository, jobRegistry, jobLauncher);
        if (locLatestRunningJobs.isEmpty()) {
            // rien faire
            return null;
        } else if (null != (locJobExecutioin = locJobExecutionService.containtCompletedStatus(locLatestRunningJobs))) {
            final Long locJobExecutionID = locJobExecutioin.getId();
            log.info("Job with id %d already running with same parameter {}", locJobExecutionID);
            // job with the same parameter is running, we stop the new job we are trying to execute
            stopJpb(parJobExecution);
            return locJobExecutionID;
        } else if ( null != (locJobExecutioin = locJobExecutionService.getFirstJobExecutioin(locLatestRunningJobs, parJobExecution.getJobParameters()))) {
            log.info("job with id %d must be executed in first : {} ", locJobExecutioin.getId());
            // récupere le plus anciaens job a execution et stopper celui accutelle
            stopJpb(parJobExecution);
            // restart locJobExecutioin
            return locJobExecutionService.restartJob(locJobExecutioin);
        }

        return null;
    }

    private void stopJpb(final JobExecution parJobExecution) {
        synchronized (parJobExecution) {
            parJobExecution.stop();
            jobRepository.update(parJobExecution);
        }
    }

}