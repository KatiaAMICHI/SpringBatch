package com.jump.configuration;

import com.jump.domain.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

@Configuration
@Slf4j
@EnableLightminEmbedded
public class CustomerJobs {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Qualifier("customerItemReader")
    @Autowired
    private ItemReader<Asset> assetItemReader;
    @Autowired
    @Qualifier("customerItemWriter")
    private ItemWriter<Asset> assetItemWriter;
    @Autowired @Qualifier("customerItemProcessor")
    private ItemProcessor<Asset, Asset> assetItemProcessor;

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
                .processor(assetItemProcessor)
                .writer(assetItemWriter)
                .build();
    }

    @StepScope
    @Bean
    @Qualifier("customerItemReader")
    public ItemReader<Asset> customerItemReader(@Value("#{jobParameters[value]}") final String resource) throws Exception {
        log.info(".................. ItemReader");
        log.info("sleep .................. 9000");
        //Thread.sleep(9000);
        log.info("end sleep .................. 9000");
        return new CustomerItemReader(resource);
    }

    @StepScope
    @Bean
    @Qualifier("customerItemProcessor")
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
    @Qualifier("customerItemWriter")
    public ItemWriter<Asset> customerItemWriter() {
        log.info(".................. ItemReader");
        return assets -> {
            if (assets != null && !assets.isEmpty()) {
                log.info("Write: {}", assets);
                // assetKVRepository.saveAll(assets);
            }
        };
    }

}
