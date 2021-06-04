package com.jump.objects.jobObject;

import lombok.*;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cache.annotation.EnableCaching;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor @NoArgsConstructor
public class JobEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long jobId;
  private Long jobExecutionId;
  private Map<String, Object> params;
  private String path;
  private BatchStatus status;
  private String exitStatus;
  private Integer channelPartition; // le num du partitionnement

}
