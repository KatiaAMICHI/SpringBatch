package com.jump;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(
		exclude = {SessionAutoConfiguration.class,
				ManagementWebSecurityAutoConfiguration.class,
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class,
		}
)
@EnableBatchProcessing
@EnableTask
@EnableTransactionManagement
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}


}
