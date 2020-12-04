package com.jump.integration;

import org.springframework.batch.core.Step;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

// @Configuration
// @EnableLightminEmbedded
// @EnableBatchIntegration
public class RemotePartitioningJobConfiguration { }

/*
    @Configuration
    public static class ManagerConfiguration {

        @Autowired
        private RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory;


         // * Configure outbound flow (requests going to workers)

        @Bean
        public DirectChannel requests() {
            return new DirectChannel();
        }


         // * Configure inbound flow (replies coming from workers)

        @Bean
        public QueueChannel replies() {
            return new QueueChannel();
        }

        @Bean
        public Step managerStep() {
                 return this.managerStepBuilderFactory
                    .get("managerStep")
                    .partitioner("workerStep", new BasicPartitioner())
                    .gridSize(10)
                    .outputChannel(requests())
                    .inputChannel(replies())
                    .build();
        }

        // Middleware beans setup omitted

    }

}
*/