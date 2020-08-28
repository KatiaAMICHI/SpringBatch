package com.jump.batch.quartz;

import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.Map;


@Configuration
@ConditionalOnExpression("'${scheduler.enabled}'=='true'")
public class QuartzConfig {
    private static final String JOB_NAME = "jobC";
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(QuartzJobLauncher.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jobName", JOB_NAME);
        map.put("jobLauncher", jobLauncher);
        map.put("jobLocator", jobLocator);
        jobDetailFactoryBean.setJobDataAsMap(map);
        jobDetailFactoryBean.setGroup("etl_group");
        jobDetailFactoryBean.setName(JOB_NAME);
        return jobDetailFactoryBean;
    }

    // tout les 1 minute
    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean() {
        CronTriggerFactoryBean ctFactory = new CronTriggerFactoryBean();
        ctFactory.setJobDetail(jobDetailFactoryBean().getObject());
        ctFactory.setStartDelay(3000);
        ctFactory.setName("cron_trigger");
        ctFactory.setGroup("cron_group");
        ctFactory.setCronExpression("0 0/1 * 1/1 * ? *");
        return ctFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(cronTriggerFactoryBean().getObject());
        scheduler.setApplicationContext(applicationContext);
        return scheduler;
    }

    @Bean
    public TriggerMonitor triggerMonitor() {
        TriggerMonitor triggerMonitor = new TriggerMonitor();
        triggerMonitor.setTrigger(cronTriggerFactoryBean().getObject());
        return triggerMonitor;
    }
}