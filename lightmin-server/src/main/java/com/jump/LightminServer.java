package com.jump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.server.annotation.EnableLightminServer;

@SpringBootApplication(exclude={BatchAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@EnableLightminServer
@EnableScheduling
@EnableLightminJdbcConfigurationRepository
public class LightminServer {
    public static void main(final String[] args) {
        SpringApplication.run(LightminServer.class, args);
    }
}
