package com.jump;

import com.jump.jobs.configs.CustomJdbcJobExecutionDao;
import com.jump.jobs.configs.JobExecutionService;
import com.jump.objects.JobEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.List;
import java.util.Objects;

@EnableBinding(Sink.class)
@Slf4j
public class ReceiveStatus {
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JdbcOperations jdbcOperations;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRegistry jobRegistry;

    @SneakyThrows
    @StreamListener(Sink.INPUT)
    public void receiveStatusOrange(final JobEvent parMsg) {
        log.info("[Server] I received a message. {}", parMsg);
        final JobExecution locJobExecution = jobExplorer.getJobExecution(parMsg.getJobExecutionId());
        assert locJobExecution != null;
        if (locJobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            return;
        }
        locJobExecution.setExitStatus(new ExitStatus(parMsg.getExitStatus()));
        jobRepository.update(locJobExecution);
        log.info("[Server] Update status JobExecution : {}", locJobExecution);

        // start next job (if present) with same name

        final CustomJdbcJobExecutionDao customJdbcJobExecutionDao = new CustomJdbcJobExecutionDao(jdbcOperations);
        final List<JobExecution> locLatestRunningJobs = customJdbcJobExecutionDao.getRunningJobExecutionsWithParams(locJobExecution.getId(), locJobExecution.getJobInstance().getJobName(), locJobExecution.getJobParameters());

        if (!locLatestRunningJobs.isEmpty()) {
            final JobExecutionService locJobExecutionService = new JobExecutionService(jobRepository, jobRegistry, jobLauncher);

            final JobExecution locLatestRunningJob = locJobExecutionService.getFirstJobExecutioin(locLatestRunningJobs, null);
            final Long locNewJobExectionId = locJobExecutionService.restartJob(locLatestRunningJob);
            log.info("[Server] create new JobExection with id : {}", locNewJobExectionId);
        }
    }





}