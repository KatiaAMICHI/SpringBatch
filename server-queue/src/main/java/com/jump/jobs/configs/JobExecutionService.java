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

import javax.validation.constraints.NotNull;
import java.util.Collections;
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
        synchronized (parFirstJobExecution.getId()) {
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

    public JobExecution getFirstJobExecutioin(@NotNull final List<JobExecution> parJobExecutions, @Nullable final JobParameters parParamJobParameters) {
        JobExecution locLatestExecution = null;

        if (parJobExecutions.isEmpty()) {
            return null;
        }
        final JobParameters locJobParameters;
        if (null == parParamJobParameters) {
            locLatestExecution = parJobExecutions.get(0);
            parJobExecutions.remove(0);
            locJobParameters = locLatestExecution.getJobParameters();
        } else {
            locJobParameters = parParamJobParameters;
        }

        long locMinDiffCreationT = Long.MAX_VALUE;
        for (final JobExecution locExecution : parJobExecutions) {
            final JobParameters locParams = locExecution.getJobParameters();

            log.warn("locParams - value1 {}", locParams.getString("value"));

            log.warn("create {}", locExecution.getCreateTime());
            log.warn("start {}", locExecution.getStartTime());

            final Long time = locJobParameters.getLong("time");
            long locDiffCreationT = (null == time ? 0 : time) - locExecution.getCreateTime().getTime();
            if (locDiffCreationT < 0) {
                log.warn("Impossible, new job executed before old! Old JobExecution id: {}", locExecution.getJobId());
                continue;
            }
            if (locDiffCreationT < locMinDiffCreationT) {
                locMinDiffCreationT = locDiffCreationT;
                locLatestExecution = locExecution;
            }
        }
        return locLatestExecution;
    }
}
