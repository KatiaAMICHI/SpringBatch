package com.jump.queue;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InterceptingJobExecution implements JobExecutionListener {
    @Autowired
    private JobLauncher jobLauncher;

    @SneakyThrows
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Intercepting Job Excution - Before Job!");
    }


    private void startJob() {
        log.info("[InterceptingJobExecution] ....... startJob");
        final JobInfo first = CacheBean.pop();
        try {
            jobLauncher.run(first.getJob(), first.getJobParameters());
        } catch (Exception ex) {
            log.error("job1 : " + ex.getMessage());
        }
    }

    @SneakyThrows
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("[InterceptingJobExecution] ....... afterJob");
        synchronized (jobExecution) {
            log.info("Intercepting Job Excution - After Job!");
            Thread.sleep(11000);

            if(CacheBean.getCacheJob().size() > 0) {
                startJob();
            }
        }
    }

}