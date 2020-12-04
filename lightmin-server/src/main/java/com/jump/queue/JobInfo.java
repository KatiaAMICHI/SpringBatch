package com.jump.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;

@RequiredArgsConstructor
@Getter @Setter
public class JobInfo {
    private final Job job;
    private final JobParameters jobParameters;
}
