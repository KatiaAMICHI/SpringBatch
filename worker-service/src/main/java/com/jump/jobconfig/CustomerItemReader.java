package com.jump.jobconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class CustomerItemReader implements ItemReader<String> {

    @Override
    public String read() throws Exception {
        log.info("reading .................");
        return "delegate.read()";
    }

}