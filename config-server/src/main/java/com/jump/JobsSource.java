package com.jump;


import com.jump.objects.JobEvent;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
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
    final JobEvent event = new JobEvent(contribution, chunkContext);
    source.output().send(MessageBuilder.withPayload(event).build());
  }
}
