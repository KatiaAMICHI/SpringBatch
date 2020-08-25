package com.jump.batch.jobs;

import com.jump.batch.obj.Asset;
import com.jump.batch.obj.AssetKVRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class CustomerJobConfiguration {
    @Autowired private JobLauncher jobLauncher;

    @Autowired private AssetKVRepository assetKVRepository;

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private ItemReader<Asset> assetItemReader;
    @Autowired private ItemWriter<Asset> assetItemWriter;
    @Autowired private ItemProcessor<Asset, Asset> assetAssetItemProcessor;

    @Bean
    public Job jobA(final JobBuilderFactory jobBuilderFactory,
                    final Step customerStep) {
        return jobBuilderFactory
                .get("jobA")
                .start(customerStep)
                .build();
    }

    @Bean
    public Step customerStep(final StepBuilderFactory stepBuilderFactory,
                             final ItemReader<Asset> assetItemReader,
                             final ItemProcessor<Asset, Asset> assetAssetItemProcessor,
                             final ItemWriter<Asset> assetItemWriter) {
        return stepBuilderFactory
                .get("customerStep")
                .<Asset, Asset>chunk(10)
                .reader(assetItemReader)
                .processor(assetAssetItemProcessor)
                .writer(assetItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<Asset> customerItemReader(@Value("#{jobParameters[value]}") final String resource) throws Exception {
        log.info(".................. ItemReader");
        return new CustomerItemReader(resource);
    }

     @Bean
    public ItemProcessor<Asset, Asset> processor() {
        log.info(".................. ItemReader");
        return new ItemProcessor<Asset, Asset>() {
            @Override
            public Asset process(Asset asset) throws Exception {
                asset.setLabel("newLabel");
                log.info("Pause for 6 seconds");
                Thread.sleep(6000);
                return asset;
            }
        };
    }

    @Bean
    public ItemWriter<Asset> customerItemWriter() {
        log.info(".................. ItemReader");
        return assets -> {
            if (assets != null && !assets.isEmpty()) {
                System.out.println("[SpringAssetItemWriter] Write: {}" + assets);
                log.info("Write: {}", assets);
                assetKVRepository.saveAll(assets);
            }
        };
    }

    // @Scheduled(fixedRate = 5000)
    /*public void runA1() throws Exception {
        log.info("[JobA] ....... runA1");
        final Step step = customerStep(stepBuilderFactory, assetItemReader, assetAssetItemProcessor, assetItemWriter);
        JobExecution execution = jobLauncher.run(jobA(jobBuilderFactory, step), new JobParametersBuilder().toJobParameters());
    }*/

    // @Scheduled(fixedRate = 5000)
    /*public void runA2() throws Exception {
        log.info("[JobA] ....... runA2");
        final Step step = customerStep(stepBuilderFactory, assetItemReader, assetAssetItemProcessor, assetItemWriter);
        JobExecution execution = jobLauncher.run(jobA(jobBuilderFactory, step), new JobParametersBuilder().toJobParameters());
    }*/

    // @Scheduled(cron = "*/1 * * * * *")
    /*public void run1(){
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);
        try {
            jobLauncher.run(job1, jobParameters);
        }catch (Exception ex){
            log.error("job1 : " + ex.getMessage());
        }

    }*/

    // @Scheduled(cron = "*/1 * * * * *")
    /*public void run2(){
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);
        try {
            jobLauncher.run(job2, jobParameters);
        }catch (Exception ex){
            log.error("job2 : " + ex.getMessage());
        }

    }*/
}
