package com.jump.objects;

import lombok.*;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.util.Map;

@Data
@AllArgsConstructor @NoArgsConstructor
public class JobEvent {
  private Map<String, Object> params;
  private String path;

}
