package com.jump.batch.jobs;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ResartJob {

    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobOperator jobOperator;

    public void restart(String jobName){
        try {
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName,0,1);// this will get one latest job from the database
            if(!jobInstances.isEmpty()){
                JobInstance jobInstance =  jobInstances.get(0);
               List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
               if(!jobExecutions.isEmpty()){
                   jobExecutions.sort((o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime()));
                   for(JobExecution execution: jobExecutions){
                       // If the job status is STARTED then update the status to FAILED and restart the job using JobOperator.java
                       if(execution.getStatus().equals(BatchStatus.STARTED)){ 
                           execution.setEndTime(new Date());
                           execution.setStatus(BatchStatus.FAILED);                               
                           execution.setExitStatus(ExitStatus.FAILED);                               
                           execution.stop();
                           jobRepository.update(execution);
                           jobOperator.restart(execution.getId());
                       }
                   }
               }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}