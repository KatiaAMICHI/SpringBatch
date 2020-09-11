package com.jump.batch.jobs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.util.Map;

@RequiredArgsConstructor
@Getter @Setter
public class JobInfo {
    private final Job job;
    private final JobParameters jobParameters;
}
