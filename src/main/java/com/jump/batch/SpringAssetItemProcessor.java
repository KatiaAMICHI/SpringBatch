package com.jump.batch;

import com.jump.batch.obj.Asset;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SpringAssetItemProcessor implements ItemProcessor<Asset, Asset>{

    @Override
    public Asset process(Asset asset) throws Exception {
        asset.setLabel("newLabel");
        return asset;
    }
}
