package com.jump.jobsconfigs;

import com.jump.objects.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(SinkTest.class)
@Slf4j
public class SinkTest {

    @StreamListener(Sink.INPUT)
    public void writeEvent(JobEvent event) {
        log.info("[SinkTest] writeEvent {}", event);
    }
}