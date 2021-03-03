package com.jump.integration;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Gateway;

@MessagingGateway
public interface IntegrationGateway {

    @Gateway(replyChannel = "replies", requestChannel = "requests")
    public String sendMessage(String message);
}
