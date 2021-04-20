package com.jump.configs;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JobConfiguration {

    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExplorer jobExplorer;

    @Bean
    public JobRegistryBeanPostProcessor jobRepositoryBeanProcessor() throws Exception {
        final JobRegistryBeanPostProcessor locPostProcessor = new JobRegistryBeanPostProcessor();
        locPostProcessor.setJobRegistry(jobRegistry);
        //locPostProcessor.afterPropertiesSet();

        return locPostProcessor;
    }

    @Bean
    public JobOperator jobOperator() throws Exception {
        final SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobExplorer(jobExplorer);
        //jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    @Bean
    public JdbcOperations jdbcOperations(final DataSource dataSource) throws Exception {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JobExplorer jobExplorer(final DataSource dataSource) throws Exception {
        final JobExplorerFactoryBean bean = new JobExplorerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTablePrefix("BATCH_");
        bean.setJdbcOperations(new JdbcTemplate(dataSource));
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        final SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        //launcher.afterPropertiesSet();
        return launcher;
    }
}
