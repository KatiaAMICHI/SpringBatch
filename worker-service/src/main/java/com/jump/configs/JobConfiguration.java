package com.jump.configs;

import lombok.SneakyThrows;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JobConfiguration extends DefaultBatchConfigurer {

    @Autowired
    private JobRegistry jobRegistry; // permet de garder une trace sur les jobs disponible dans le contexte
    @Autowired
    private JobLauncher jobLauncher; // permet d'executer un job et d'avoir l'instance du JobExecution associer
    @Autowired
    private JobRepository jobRepository; // permet de recuperer des infos de la base et de faire des modifications
    @Autowired
    private JobExplorer jobExplorer; // permet de taper dans la base pour avori des infos sur un job, step, jobexecution ....
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PlatformTransactionManager transactionManager;

    /*@Bean
    public JobOperator jobOperator() throws Exception {
        final SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    @Bean
    public JdbcOperations jdbcOperations() {
        return new JdbcTemplate(dataSource);
    }
*/
    @SneakyThrows
    @Override
    public JobExplorer getJobExplorer() {
        final JobExplorerFactoryBean bean = new JobExplorerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTablePrefix("BATCH_");
        bean.setJdbcOperations(new JdbcTemplate(dataSource));
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @SneakyThrows
    @Override
    public JobLauncher getJobLauncher() {
        final SimpleJobLauncher locLauncher = new SimpleJobLauncher();
        locLauncher.setJobRepository(jobRepository);
        locLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        locLauncher.afterPropertiesSet();
        return locLauncher;
    }

    /*@Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(15);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(30);
        return taskExecutor;
    }*/

    @SneakyThrows
    @Override
    public JobRepository getJobRepository() {
        final JobRepositoryFactoryBean locFactory = new JobRepositoryFactoryBean();
        locFactory.setDataSource(dataSource);
        locFactory.setTransactionManager(transactionManager);
        locFactory.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED");
        locFactory.setValidateTransactionState(false);
        locFactory.setSerializer(new DefaultExecutionContextSerializer());
        locFactory.afterPropertiesSet();
        return locFactory.getObject();
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory(){
        return new JobBuilderFactory(jobRepository);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    // erreur JPA sans la configue
    /*@Bean
    public BatchConfigurer configurer(){
        return new DefaultBatchConfigurer(dataSource) {
            @SneakyThrows
            @Override
            public JobRepository getJobRepository() {
                return createJobRepository();
            }
        };
    }*/

}
