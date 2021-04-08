package com.jump;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
@EnableBatchProcessing
@EnableTask
public class WorkerServiceApplication {

	public static void main(String[] args) {
		// notifToServer();
		SpringApplication.run(WorkerServiceApplication.class, args);
	}
}
