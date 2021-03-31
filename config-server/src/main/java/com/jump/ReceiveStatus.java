package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@Slf4j
public class ReceiveStatus {
    @StreamListener(Sink.INPUT)
    public void receiveStatusOrange(String msg) {
       log.info("[Server] I received a message. It was orange number: {}", msg);
    }
}