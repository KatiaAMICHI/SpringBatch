package com.jump.batch;

import com.jump.batch.obj.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

import javax.sql.DataSource;
import java.util.List;

public class SpringBatchConfig {

    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private ItemReader<Asset> assetItemReader;
    @Autowired private ItemWriter<Asset> assetItemWriter;
    @Autowired private ItemProcessor<Asset, Asset> assetItemProcessor;

    @Bean
    public Job customerJob(final JobBuilderFactory jobBuilderFactory,
                           final Step customerStep) {
        return jobBuilderFactory
                .get("customerJob")
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


    @Bean
    public ItemReader<Asset> customerItemReader(@Value("${inputFile}") final Resource resource) throws Exception {
        final FlatFileItemReader<Asset> assetFlatFileItemReader = new FlatFileItemReader<Asset>();
        assetFlatFileItemReader.setName("AssetFile");
        assetFlatFileItemReader.setLinesToSkip(1);
        assetFlatFileItemReader.setResource(resource);
        assetFlatFileItemReader.setLineMapper(lineMapping());
        return assetFlatFileItemReader;
    }

    @Bean
    public LineMapper<Asset> lineMapping() {
        DefaultLineMapper<Asset> assetLineMapper = new DefaultLineMapper<Asset>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("name,label");
        assetLineMapper.setLineTokenizer(delimitedLineTokenizer);
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(Asset.class);
        assetLineMapper.setFieldSetMapper(fieldSetMapper);
        return assetLineMapper;
    }

}
