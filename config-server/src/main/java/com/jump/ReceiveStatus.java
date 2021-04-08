package com.jump;

import com.jump.objects.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@EnableBinding(Sink.class)
@Slf4j
public class ReceiveStatus {
    @Output(Processor.OUTPUT)
    MessageChannel output;
    @Autowired
    SubscribableChannel input;

    @StreamListener(Sink.INPUT)
    public void receiveStatusOrange(JobEvent msg) {
       // input.subscribe(output);
       log.info("[Server] I received a message. {}", msg);
    }
}