package com.jump.jobs.configs;

import com.sun.istack.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class JobExecutionService {

    private final JobRepository jobRepository;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    public JobExecution containtCompletedStatus(final List<JobExecution> parJobNotFinish) {
        for (final JobExecution locJobExecution : parJobNotFinish) {
            if (locJobExecution.getStatus() == BatchStatus.COMPLETED) {
                return locJobExecution;
            }
        }
        return null;
    }

    @SneakyThrows
    public Long restartJob(final JobExecution parFirstJobExecution) {
        synchronized (parFirstJobExecution) {
            log.info("Checking status of job execution with id=" + parFirstJobExecution.getId());
            String jobName = parFirstJobExecution.getJobInstance().getJobName();
            final Job job = jobRegistry.getJob(jobName);
            final JobParameters parameters = Objects.requireNonNull(job.getJobParametersIncrementer())
                                                    .getNext(parFirstJobExecution.getJobParameters());
            log.info(String.format("Attempting to resume job with name=%s and parameters=%s", jobName, parameters));

            parFirstJobExecution.setExitStatus(ExitStatus.NOOP);
            jobRepository.update(parFirstJobExecution);
            try {
                return jobLauncher.run(job, parameters).getId();
            } catch (JobExecutionAlreadyRunningException var8) {
                throw new UnexpectedJobExecutionException(String.format(
                        "Illegal state (only happens on a race condition): %s with name=%s and parameters=%s",
                        "job execution already running",
                        jobName,
                        parameters), var8);
            }
        }
    }

    public JobExecution getFirstJobExecutioin(final List<JobExecution> parJobExecutions, @Nullable final JobParameters parParamJobParameters) {
        JobExecution latestExecution = null;

        if (parJobExecutions.isEmpty()) {
            return null;
        }
        final JobParameters locJobParameters;
        if (null == parParamJobParameters) {
            latestExecution = parJobExecutions.get(0);
            parJobExecutions.remove(0);
            locJobParameters = latestExecution.getJobParameters();
        } else {
            locJobParameters = parParamJobParameters;
        }

        long minDiff = Long.MAX_VALUE;
        for (final JobExecution execution : parJobExecutions) {
            final JobParameters params = execution.getJobParameters();

            log.warn("params - value1 {}", params.getString("value"));

            log.warn("create {}", execution.getCreateTime());
            log.warn("start {}", execution.getStartTime());

            final Long time = locJobParameters.getLong("time");
            long diff = (null == time ? 0 : time) - execution.getCreateTime().getTime();
            if (diff < 0) {
                log.warn("Impossible, new job executed before old! Old JobExecution id: {}", execution.getJobId());
                continue;
            }
            if (diff < minDiff) {
                minDiff = diff;
                latestExecution = execution;
            }
        }
        return latestExecution;
    }
}
