package com.jump;

import com.jump.jobs.configs.InterceptingJobExecution;
import com.jump.objects.JobEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.UnexpectedJobExecutionException;
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
    private JobOperator jobOperator;
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
    public void receiveStatusOrange(JobEvent msg) {
       // input.subscribe(output);
       log.info("[Server] I received a message. {}", msg);
    }
}