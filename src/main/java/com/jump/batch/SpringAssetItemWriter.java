package com.jump.batch;

import com.jump.batch.obj.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringAssetItemWriter implements ItemWriter<Asset>{
    private static Logger log = LoggerFactory.getLogger(SpringAssetItemWriter.class);


    @Override
    public void write(List<? extends Asset> list) throws Exception {
        log.info("Write: {}", list);
    }
}
