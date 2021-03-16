package com.jump.jobs;

//import com.jump.task.AppendingCommandLineArgsProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.cloud.deployer.spi.task.TaskLauncher;


@Configuration
public class CustomJobs {

	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/*@Bean
	public Partitioner partitioner() throws IOException {
		Resource[] resources =
				this.resourcePatternResolver.getResources("s3://connected-car-artifacts/inputs/*.csv");

		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		partitioner.setResources(resources);

		return partitioner;
	}

	@Bean
	public AppendingCommandLineArgsProvider commandLineArgsProvider() {
		AppendingCommandLineArgsProvider provider = new AppendingCommandLineArgsProvider();

		List<String> commandLineArgs = new ArrayList<>(4);
		commandLineArgs.add("--spring.profiles.active=worker");
		commandLineArgs.add("--spring.cloud.task.initialize.enable=false");
		commandLineArgs.add("--spring.batch.initializer.enabled=false");
		commandLineArgs.add("--spring.datasource.initialize=false");
		provider.addCommandLineArgs(commandLineArgs);

		return provider;
	}

	@Bean
	public DeployerPartitionHandler partitionHandler(
			final TaskLauncher taskLauncher,
			final JobExplorer jobExplorer,
			final CommandLineArgsProvider commandLineArgsProvider
	) {
		DeployerPartitionHandler partitionHandler =
				new DeployerPartitionHandler(taskLauncher,
						jobExplorer,
						context.getResource("http://github.com/mminella/task_jars/raw/master/s3jdbc-0.0.1-SNAPSHOT.jar"),
						"load");

		partitionHandler.setCommandLineArgsProvider(commandLineArgsProvider);
		partitionHandler.setEnvironmentVariablesProvider(new NoOpEnvironmentVariablesProvider());
		partitionHandler.setMaxWorkers(2);
		partitionHandler.setApplicationName("S3LoaderJob");

		return partitionHandler;
	}*/


	@Bean
	public Step master() {
		return stepBuilderFactory.get("master")
				.partitioner("load", null)
				//.partitionHandler(partitionHandler)
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("s3jdbc")
				.start(master())
				.build();
	}
}
