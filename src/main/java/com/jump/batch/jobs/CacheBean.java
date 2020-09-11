package com.jump.batch.jobs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;

import java.util.LinkedList;
import java.util.Queue;

@NoArgsConstructor

public class CacheBean {

    @Getter(AccessLevel.PUBLIC) private static Queue<JobInfo> cacheJob = new LinkedList<>();

    public static void addToMap(Job parJob, JobParameters parJobParameter) {
        cacheJob.add(new JobInfo(parJob, parJobParameter));
    }

    public static JobInfo pop() {
        return cacheJob.poll();
    }

}