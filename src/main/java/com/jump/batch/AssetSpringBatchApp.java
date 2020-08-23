package com.jump.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableTransactionManagement
@EnableLightminEmbedded
public class AssetSpringBatchApp {

    public static void main(String[] args) {
        SpringApplication.run(AssetSpringBatchApp.class);

    }
}
