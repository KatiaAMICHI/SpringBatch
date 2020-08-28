package com.jump.batch.jobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.JobParameters;

@Data
@AllArgsConstructor
public class JobInformation {
    private String name;
    private JobParameters jobParameters;
}
