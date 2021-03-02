package com.jump.configuration;

import com.jump.domain.Asset;
import com.jump.domain.AssetKVRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Slf4j
public class CustomerItemReader implements ItemReader<Asset> {

    @Autowired
    private AssetKVRepository assetKVRepository;

    private final String value;

    private ItemReader<Asset> delegate;

    public CustomerItemReader(final String value) {
        this.value = value;
    }

    @Override
    public Asset read() throws Exception {
        log.info("reading .................");
        if (delegate == null) {
            delegate = new IteratorItemReader<Asset>(assets());
        }
        return delegate.read();
    }

    private List<Asset> assets() {
        final Asset locAsset = assetKVRepository.getByLabel(value);
        return Collections.singletonList(locAsset);
    }
}