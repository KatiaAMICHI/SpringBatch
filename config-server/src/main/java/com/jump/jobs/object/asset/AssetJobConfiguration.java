package com.jump.jobs.object.asset;

import com.jump.jobs.configs.InterceptingJobExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@EnableBinding(Source.class)
public class AssetJobConfiguration {
  @Autowired
  public JobBuilderFactory jobBuilderFactory;
  @Autowired
  public StepBuilderFactory stepBuilderFactory;
  @Autowired
  private Source sources;
  @Autowired
  private JobRegistry jobRegistry;
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
  private PlatformTransactionManager platformTransactionManager;

  @Bean @Qualifier("jobasset")
  public Job job() {
    RunIdIncrementer jobParametersIncrementer = new RunIdIncrementer();
    return jobBuilderFactory
            .get("jobasset")
            .start(workerStep())
            .incrementer(jobParametersIncrementer)
            .listener(new InterceptingJobExecution(jobExplorer, jobOperator, jobRepository, jdbcOperations, jobRegistry, jobLauncher))
            .build();
  }

  @Bean
  public Step workerStep() {
      return this.stepBuilderFactory
              .get("jobStep1")
              .tasklet(new AssetTasklet(sources))
              //.transactionManager(platformTransactionManager)
              .build();
  }

}