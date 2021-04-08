package com.jump;

import com.jump.objects.JobEvent;
import com.jump.objects.asset.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.client.RestTemplate;

@EnableBinding(Processor.class)
@Slf4j
public class ReceiveStatusPro {

    @StreamListener(Processor.INPUT) @SendTo(Processor.OUTPUT)
    public JobEvent listen(@Payload JobEvent in, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws InterruptedException {
        Thread.sleep(10000);
        log.info("[Worker] received message : " + in + ", from partition " + partition);
        Asset locResult = getResult(in.getPath());

        log.info("[Worker] result : " + locResult);
        in.setStatus(BatchStatus.COMPLETED);
        return in;
    }

    public Asset getResult(final String parUrl) {
        RestTemplate restTemplate = new RestTemplate();
        Asset result = restTemplate.getForObject(parUrl, Asset.class);

        return result;
    }
}