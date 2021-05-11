package com.jump.objects.jobObject;

import com.jump.configs.CacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(path = "/jobevents")
public class Controller {

    @Autowired
    private CacheClient _cacheClient;

    @GetMapping("/getAll")
    public Collection<JobEvent> getAll() {
        return _cacheClient.getAll();
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobEvent get(@RequestParam(name = "jobid") final Long parJobId) {
        return _cacheClient.get(parJobId);
    }
}