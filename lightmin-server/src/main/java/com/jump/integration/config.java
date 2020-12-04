package com.jump.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

//@Configuration
public class config {

    @Bean("inputChannel")
    public MessageChannel inputChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel outputChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow outBoundFlow() {
        System.out.println("Inside t outBoundFlow flow ");
        final String uri = "http://localhost:9012/lightmin-client";
        return f -> f.channel(inputChannel())
                     .handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET).expectedResponseType(String.class))
                     .channel(outputChannel());
    }
}
