/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jump.jobsconfigs;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobConfiguration {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer, JobRepository jobRepository) {
		return new DeployerStepExecutionHandler(this.context, jobExplorer, jobRepository);
	}

	@Bean
	@StepScope
	public DownloadingStepExecutionListener downloadingStepExecutionListener() {
		return new DownloadingStepExecutionListener();
	}



	@Bean
	public Step load(StepExecutionListener listener) {
		return this.stepBuilderFactory
				.get("load")
				.tasklet(tasklet(null))
				.listener(listener)
				.build();
	}

	@Bean
	@StepScope
	public Tasklet tasklet(@Value("#{stepExecutionContext['path']}") final String path) {
		return (contribution, chunkContext) -> {
			System.out.println("processing " + path);
			return RepeatStatus.FINISHED;
		};
	}
}
