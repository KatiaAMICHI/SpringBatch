package com.jump.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;

@SpringBootApplication
@EnableScheduling
@EnableLightminEmbedded
@EnableLightminJdbcConfigurationRepository
@EnableJpaRepositories
public class AssetSpringBatchApp {

    public static void main(String[] args) {
        SpringApplication.run(AssetSpringBatchApp.class);

    }
}