package com.jump.objects;

import lombok.*;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Map;

@Data
@AllArgsConstructor @NoArgsConstructor
public class JobEvent {
  private Long jobId;
  private Long jobExecutionId;
  private Map<String, Object> params;
  private String path;
  private BatchStatus status;
  private String exitStatus;

}
