package com.jump.objects;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ConnectionNotif {
    String OUTPUT = "output";

    @Output("output")
    MessageChannel output();

}
