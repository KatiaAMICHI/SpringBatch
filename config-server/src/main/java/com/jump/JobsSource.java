package com.jump;


import com.jump.objects.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@EnableBinding(Source.class)
@Slf4j
public class JobsSource {
  @Autowired
  private Source source;

  /*@Bean
  @InboundChannelAdapter(
          value = Source.OUTPUT,
          poller = @Poller(fixedDelay = "10000", maxMessagesPerPoll = "1")
  )
  public MessageSource<Long> timeMessageSource() {
    return () -> MessageBuilder.withPayload(new Date().getTime()).build();
  }*/

  public void send(StepContribution contribution, ChunkContext chunkContext) {

    Message<ChunkContext> msg = new GenericMessage<>(chunkContext);
    log.info("msg to send : " + msg);
    source.output().send(msg);

    //source.output().send(MessageBuilder.withPayload(event).build());
  }


  public void send(StepExecution stepExecution) {

    Message<StepExecution> msg = new GenericMessage<>(stepExecution);
    log.info("msg to send : " + msg);
    source.output().send(msg);

    //source.output().send(MessageBuilder.withPayload(event).build());
  }
}
