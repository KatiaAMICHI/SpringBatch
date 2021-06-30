package com.jump.configs;

import lombok.SneakyThrows;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JobConfiguration {

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

    /*@Bean
    @Primary
    public ObjectMapper getJacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.addMixInAnnotations(org.springframework.batch.core.StepContribution.class, StepContributionMixin.class);
        objectMapper.addMixInAnnotations(org.springframework.batch.core.StepExecution.class, StepExecutionMixin.class);
        objectMapper.addMixInAnnotations(org.springframework.batch.core.JobExecution.class, JobExecutionMixin.class);
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }*/

    @Bean
    public JobRegistryBeanPostProcessor jobRepositoryBeanProcessor() throws Exception {
        final JobRegistryBeanPostProcessor locPostProcessor = new JobRegistryBeanPostProcessor();
        locPostProcessor.setJobRegistry(jobRegistry);
        locPostProcessor.afterPropertiesSet();

        return locPostProcessor;
    }

    @Bean
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

    @Bean
    public JobExplorer jobExplorer() throws Exception {
        final JobExplorerFactoryBean bean = new JobExplorerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTablePrefix("BATCH_");
        bean.setJdbcOperations(new JdbcTemplate(dataSource));
        bean.afterPropertiesSet();
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        final SimpleJobLauncher locLauncher = new SimpleJobLauncher();
        locLauncher.setJobRepository(jobRepository);
        locLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        locLauncher.afterPropertiesSet();
        return locLauncher;
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    protected JobRepository createJobRepository() throws Exception {
        final JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED");
        factory.setValidateTransactionState(false);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository){
        return new JobBuilderFactory(jobRepository);
    }

    // erreur JPA sans la configue
    @Bean
    public BatchConfigurer configurer(){
        return new DefaultBatchConfigurer(dataSource) {
            @SneakyThrows
            @Override
            public JobRepository getJobRepository() {
                return createJobRepository();
            }
        };
    }
}
