package com.jump;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/queue")
@Tag(name = "Queue", description = "description jobs")
@Slf4j
public class QueueController {

    @Autowired
    @Qualifier("jobC")
    private Job job;

    @GetMapping("/load")
    public BatchStatus Load(@RequestParam("value") String parValue) throws URISyntaxException {
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("value", new JobParameter(parValue));
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        final String url = "http://localhost:9061/job/load";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = new HashMap<>();
        map.put("job", job);
        map.put("params", jobParameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        restTemplate.postForEntity(url, entity, BatchStatus.class);
        return BatchStatus.UNKNOWN;
    }

}
