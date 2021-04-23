package com.jump.jobs.object.asset;

import com.jump.jobs.configs.InterceptingJobExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;

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
  private JobRepository jobRepository;
  @Autowired
  private JdbcOperations jdbcOperations;
  @Autowired
  private JobLauncher jobLauncher;

  @Bean @Qualifier("jobasset")
  public Job job() {
    return jobBuilderFactory
            .get("jobasset")
            .start(workerStep())
            .incrementer(new RunIdIncrementer())
            .listener(new InterceptingJobExecution(jobRepository, jdbcOperations, jobRegistry, jobLauncher))
            .build();
  }

  @Bean
  public Step workerStep() {
      return this.stepBuilderFactory
              .get("jobStep1")
              .tasklet(new AssetTasklet(sources))
              .build();
  }

}