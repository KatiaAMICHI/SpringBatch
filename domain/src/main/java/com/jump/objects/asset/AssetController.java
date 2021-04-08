package com.jump.objects.asset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("asset/")
@Slf4j
public class AssetController {

    @Autowired
    private AssetKVRepository _repository;

    @GetMapping("/getAll")
    public List<Asset> getAll() {
        log.info("try to get Asset by label");
        return _repository.findAll();
    }

    @GetMapping("/get")
    public Asset getByLabel(@RequestParam(value = "label") final String parLabel) {
        log.info("try to get Asset by label");
        return _repository.getByLabel(parLabel);
    }

    @PostMapping("/update")
    public Asset updateLabel(@RequestParam(value = "label") final String parLabel, @RequestParam(value = "newlabel") final String parNewLabel) {
        log.info("try to update Asset with new label");
        final Asset locAsset = _repository.getByLabel(parLabel);
        locAsset.setLabel(parNewLabel);
        return locAsset;
    }

    @PutMapping("/add")
    public Asset addAsset(@RequestParam(value = "label") final String parLabel) {
        log.info("try to update Asset with new label");
        final Asset locAsset = new Asset();
        locAsset.setLabel(parLabel);
        return _repository.save(locAsset);
    }

}
