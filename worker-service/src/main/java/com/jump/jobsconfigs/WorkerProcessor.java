package com.jump.jobsconfigs;

import com.jump.objects.JobEvent;
import com.jump.objects.asset.AssetController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

@EnableBinding(Processor.class)
@Slf4j
public class WorkerProcessor {

  @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
  public Object wish(JobEvent event) {
    // faire le traitement ici, executer la requete qui sera passer en parametre
    log.info("[WorkerServer] Processing event {}", event);

    new AssetController().makeAWish(null);
      log.info("The child made a wish {}.");

    return null;
  }

}
