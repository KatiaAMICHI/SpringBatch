package com.jump.remotechunking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;

@Configuration
public class ChunkingConfig {
    public static String TOPIC_REQUESTS = "step-execution-eventslol_chunking_requests";
    public static String TOPIC_REPLIES = "step-execution-eventslol_chunking_replies";
    public static String GROUP_ID = "stepresponse_chunking";

    @Bean
    public DirectChannel requests_chunking() {
        return new DirectChannel();
    }

    @Bean
    public QueueChannel replies_chunking() {
        return new QueueChannel();
    }



}
