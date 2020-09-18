package com.jump.configuration;

import com.jump.domain.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

import javax.sql.DataSource;

@Configuration
@Slf4j
//@EnableLightminEmbedded
public class CustomerJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ItemReader<Asset> assetItemReader;
    @Autowired
    private ItemWriter<Asset> assetItemWriter;
    @Autowired
    private ItemProcessor<Asset, Asset> assetAssetItemProcessor;

    @Bean("job")
    @Primary
    public Job job() {
        return jobBuilderFactory
                .get("job")
                .start(customerStep())
                .build();
    }

    @Bean("job0")
    public Job job0() {
        return jobBuilderFactory
                .get("job0")
                .start(customerStep())
                .build();
    }

    @Bean("jobA")
    public Job jobA() {
        return jobBuilderFactory
                .get("jobA")
                .start(customerStep())
                .build();
    }

    @Bean("jobB")
    public Job jobB() {
        return jobBuilderFactory
                .get("jobB")
                .start(customerStep())
                .build();
    }

    @Bean("jobC")
    public Job jobC() {
        return jobBuilderFactory
                .get("jobC")
                .start(customerStep())
                .build();
    }

    @Bean
    public Step customerStep() {
        return stepBuilderFactory
                .get("customerStep")
                .<Asset, Asset>chunk(1)
                .reader(assetItemReader)
                .processor(assetAssetItemProcessor)
                .writer(assetItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<Asset> customerItemReader(@Value("#{jobParameters[value]}") final String resource) throws Exception {
        log.info(".................. ItemReader");
        log.info("sleep .................. 9000");
        //Thread.sleep(9000);
        log.info("end sleep .................. 9000");
        return new CustomerItemReader(resource);
    }

    @StepScope
    @Bean
    public ItemProcessor<Asset, Asset> processor() {
        log.info(".................. ItemReader");
        return new ItemProcessor<Asset, Asset>() {
            @Override
            public Asset process(Asset asset) throws Exception {
                //asset.setLabel("newLabel");
                log.info("Pause for 6 seconds");
                //Thread.sleep(6000);
                return asset;
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<Asset> customerItemWriter() {
        log.info(".................. ItemReader");
        return assets -> {
            if (assets != null && !assets.isEmpty()) {
                log.info("Write: {}", assets);
                // assetKVRepository.saveAll(assets);
            }
        };
    }

    @Bean
    public JobOperator jobOperator(final JobLauncher jobLauncher, final JobRepository jobRepository,
                                   final JobRegistry jobRegistry, final JobExplorer jobExplorer) {
        final SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobExplorer(jobExplorer);
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

}
