package com.jump.batch.jobs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.JobParameter;

import java.util.Map;

@NoArgsConstructor
@Getter @Setter
public class JobInfoDB {
    private Long id;
    private String name;
    private String status;
    private Map<String, JobParameter> mapParameters;
}
