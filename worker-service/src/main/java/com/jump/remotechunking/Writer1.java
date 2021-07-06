package com.jump.remotechunking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Writer1 implements ItemStreamWriter<Object>, ItemWriter<Object> {
    @Override
    public void write(List<? extends Object> list) throws Exception {
        log.info("Before SLEEP 6 S >> Write: {}", list);
        Thread.sleep(6000);
        log.info("Write: {}", list);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}