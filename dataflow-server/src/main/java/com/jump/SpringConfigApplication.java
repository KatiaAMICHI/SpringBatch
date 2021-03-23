package com.jump;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.dataflow.autoconfigure.local.LocalDataFlowServerAutoConfiguration;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;
import org.springframework.cloud.kubernetes.KubernetesAutoConfiguration;
import org.springframework.cloud.task.configuration.MetricsAutoConfiguration;

@SpringBootApplication
		(exclude = {
				CloudFoundryDeployerAutoConfiguration.class,

				/*MetricsAutoConfiguration.class,
				SessionAutoConfiguration.class,
				ManagementWebSecurityAutoConfiguration.class,
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class,
				LocalDeployerAutoConfiguration.class,
				CloudFoundryDeployerAutoConfiguration.class,
				KubernetesAutoConfiguration.class,
				LocalDataFlowServerAutoConfiguration.class */
		})
@EnableDataFlowServer
public class SpringConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringConfigApplication.class, args);
	}

}
