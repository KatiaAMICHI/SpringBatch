package com.jump.batch.jobs;

import com.jump.batch.obj.Asset;
import com.jump.batch.obj.AssetKVRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;


public class CustomerItemReader implements ItemReader<Asset> {

    @Autowired private AssetKVRepository assetKVRepository;

    private final String value;

    private ItemReader<Asset> delegate;

    public CustomerItemReader(final String value) {
        this.value = value;
    }

    @Override
    public Asset read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<Asset>(assets());
        }
        return delegate.read();
    }

    private List<Asset> assets() throws FileNotFoundException {
        final Asset locAsset = assetKVRepository.getByLabel(value);
        return Collections.singletonList(locAsset);
    }
}