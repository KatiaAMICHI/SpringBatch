package com.jump.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

@Getter @Setter @AllArgsConstructor
public class JobEvent {
  private StepContribution obj;
  private ChunkContext availability;

  @Override
  public String toString() {
    return "ObjectJob{"
      + obj +
      ", " + availability +
      '}';
  }
}
