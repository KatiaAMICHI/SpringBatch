package com.jump.integration;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class IntegrationServiceServer {

    @ServiceActivator(inputChannel = "integration.gateway.channel")
    public void annotherMessage(Message<String> message) throws MessagingException {
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        MessageBuilder.fromMessage(message);
        Message<String> newMessage = MessageBuilder.withPayload("Message from server : " + message.getPayload()).build();
        replyChannel.send(newMessage);
    }
}
