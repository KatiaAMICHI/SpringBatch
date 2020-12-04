package com.jump.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Slf4j
@MessageEndpoint
public class MessageReceiver {

  @ServiceActivator(inputChannel = "requests")
  public void receiveMessage(final TextMessage message) throws JMSException {
    log.info("[MessageReceiver] ................. receiveMessage");
    // Do something with message.getText()
  }
}