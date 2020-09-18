package com.jump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuxdevelop.spring.batch.lightmin.client.classic.annotation.EnableLightminClientClassic;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;

@EnableScheduling
@EnableLightminClientClassic
@EnableLightminJdbcConfigurationRepository
@SpringBootApplication(exclude = {BatchAutoConfiguration.class})
public class ClientApp {

    public static void main(final String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

}
