package com.jump.jobs.object.asset;

import com.jump.objects.jobObject.JobEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * definition du tasklet d'un job
 *      le message pour le worker est envoyé dans #execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
 */
@Slf4j
@RequiredArgsConstructor
public class AssetTasklet implements Tasklet, StepExecutionListener {
    private final Source source;

    @Override
    public void beforeStep(final StepExecution parStepExecution) {
        log.debug("[BEFORE STEP] Asset tasket initialized");
    }

    @Override
    public RepeatStatus execute(final StepContribution parStepContribution, final ChunkContext parChunkContext) {

        // soucis avec la deserialisation du JobParameter quand le worker reçoit le message
        final Map<String, Object> locParameters =
                parStepContribution.getStepExecution().getJobParameters().getParameters()
                                .entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
        final long locJobId = parChunkContext.getStepContext().getStepExecution().getJobExecution().getJobId();
        final BatchStatus locStatus = parChunkContext.getStepContext().getStepExecution().getJobExecution().getStatus();

        final Long locJobExecutionId = parChunkContext.getStepContext().getStepExecution().getJobExecution().getId();
        final String locPath = "http://localhost:1111/asset/get?label=" + locParameters.get("value");

        final JobEvent locPayload = new JobEvent(locJobId, locJobExecutionId, locParameters, locPath, locStatus, "UNKNOWN", null);
        final Message<JobEvent> locPartitionKey = MessageBuilder.withPayload(locPayload)
                                                             .setHeader("custom_info", "start")
                                                             .build();
        log.info("AVANY  sendsendsendsendsendsendsendsendsendsendsendsendsend : ");
        final boolean send = source.output().send(locPartitionKey);
        //log.info("APRES sendsendsendsendsendsendsendsendsendsendsendsendsend : " + send);

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(final StepExecution parStepExecution) {
        log.debug("[AfterStep] Job executing in remote server");
        return ExitStatus.UNKNOWN;
    }

}