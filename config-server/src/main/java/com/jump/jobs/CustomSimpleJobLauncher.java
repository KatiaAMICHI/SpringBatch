package com.jump.jobs;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class CustomSimpleJobLauncher extends JdbcJobInstanceDao {
    private final JdbcOperations jdbcOperations;

    @Nullable
    public synchronized Boolean canRunJob(@NotNull final Job job, @NotNull final JobParameters jobParameters) {
          log.info("[CustomSimpleJobLauncher] ....... canRunJob");
        final RowMapper<JobInfoDB> locRowMapper = new JobInstanceRowMapper();
        final List<JobInfoDB> instances = jdbcOperations.query(this.getQuery("SELECT ji.JOB_INSTANCE_ID, ji.JOB_NAME, je.STATUS, jep.KEY_NAME, jep.STRING_VAL from %PREFIX%JOB_INSTANCE ji, %PREFIX%JOB_EXECUTION je, %PREFIX%JOB_EXECUTION_PARAMS jep where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID and STATUS = ?  and jep.KEY_NAME = ? GROUP BY ji.JOB_INSTANCE_ID"), locRowMapper, new Object[]{"STARTED", "value"});

        final JobParameter value = jobParameters.getParameters().get("value");

        for(JobInfoDB locJobInfoDB : instances) {
            if(locJobInfoDB.getMapParameters().get("value").equals(value)) {
                // CacheBean.addToMap(job, jobParameters);
                break;
            }
        }

        // return CacheBean.getCacheJob().isEmpty();
        return null;
    }

    @NoArgsConstructor
    private final class JobInstanceRowMapper implements RowMapper<JobInfoDB> {
        @SneakyThrows
        public JobInfoDB mapRow(ResultSet rs, int rowNum) throws SQLException {
            JobInfoDB jobInfoDB = new JobInfoDB();
            jobInfoDB.setId(rs.getLong(1));
            jobInfoDB.setName(rs.getString(2));
            jobInfoDB.setStatus(rs.getString(3));

            // 4 >> KEY_NAME

            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("value", new JobParameter(rs.getString(5)));
            jobInfoDB.setMapParameters(confMap);

            return jobInfoDB;
        }
    }

    @NoArgsConstructor
    @Getter @Setter
    public class JobInfoDB {
        private Long id;
        private String name;
        private String status;
        private Map<String, JobParameter> mapParameters;
    }

}