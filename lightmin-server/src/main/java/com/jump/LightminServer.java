package com.jump;

import com.jump.integration.BasicPartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;
import org.tuxdevelop.spring.batch.lightmin.server.annotation.EnableLightminServer;
import javax.annotation.PostConstruct;
import org.apache.activemq.broker.BrokerService;

@SpringBootApplication(exclude={BatchAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@EnableLightminServer
@EnableScheduling
@EnableLightminJdbcConfigurationRepository
@Import({BasicPartitioner.class})
public class LightminServer {
    @Value("${broker.url}")
    private String brokerUrl;

    public static void main(final String[] args) {
        SpringApplication.run(LightminServer.class, args);
    }

    @PostConstruct
    public void init() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector(brokerUrl);
        broker.start();
    }
}