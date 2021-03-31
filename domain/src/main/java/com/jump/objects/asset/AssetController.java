package com.jump.objects.asset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("asset/")
@Slf4j
public class AssetController {

    @Autowired
    private AssetKVRepository _repository;

    @GetMapping("/get")
    public Asset getByLabel(@PathVariable(value = "label") final String parLabel) {
        log.info("try to get Asset by label");
        return _repository.getByLabel(parLabel);
    }

    @PostMapping("/update")
    public Asset updateLabel(@PathVariable(value = "label") final String parLabel, @PathVariable(value = "newlabel") final String parNewLabel) {
        log.info("try to update Asset with new label");
        final Asset locAsset = _repository.getByLabel(parLabel);
        locAsset.setLabel(parNewLabel);
        return locAsset;
    }

    @PutMapping("/add")
    public Asset addAsset(@PathVariable(value = "label") final String parLabel) {
        log.info("try to update Asset with new label");
        final Asset locAsset = new Asset();
        locAsset.setLabel(parLabel);
        return _repository.save(locAsset);
    }

    public void makeAWish(Object obj) {
        log.info(" [ AssetController ] do samthing ....., obj : " + obj);
        // return new Wish(toy, "Wawwww, this toy is wonderful! Santa please bring it " + "to me.");
    }
}
