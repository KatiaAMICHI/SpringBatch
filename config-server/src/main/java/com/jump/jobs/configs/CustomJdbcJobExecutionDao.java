package com.jump.jobs.configs;

import com.jump.jobs.JobInfoDB;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class CustomJdbcJobExecutionDao extends JdbcJobExecutionDao {
    private final JdbcOperations jdbcOperations;

    public List<JobExecution> getRunningJobExecutions(final String parJobName) {
        log.info("[CustomSimpleJobLauncher] ....... get Job running");
        /*final Set<JobExecution> result = new HashSet<>();
        RowCallbackHandler handler = new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                JobExecutionRowMapper mapper = new JobExecutionRowMapper(jdbcOperations);
                result.add(mapper.mapRow(rs, 0));
            }
        };
        jdbcOperations.query(this.getQuery("SELECT E.JOB_EXECUTION_ID, E.START_TIME, E.END_TIME, E.STATUS, E.EXIT_CODE, E.EXIT_MESSAGE, E.CREATE_TIME, E.LAST_UPDATED, E.VERSION, E.JOB_INSTANCE_ID, E.JOB_CONFIGURATION_LOCATION from %PREFIX%JOB_EXECUTION E, %PREFIX%JOB_INSTANCE I where E.JOB_INSTANCE_ID=I.JOB_INSTANCE_ID and I.JOB_NAME=? and E.START_TIME is not NULL and E.END_TIME is NULL order by E.JOB_EXECUTION_ID desc"), new Object[]{"jobasset"}, handler);
        */

        JobExecutionRowMapper mapper = new JobExecutionRowMapper(jdbcOperations);
        return jdbcOperations.query(
                this.getQuery(
                        "SELECT je.JOB_EXECUTION_ID, je.START_TIME, je.END_TIME, je.STATUS, je.EXIT_CODE, je.EXIT_MESSAGE, je.CREATE_TIME, je.LAST_UPDATED, je.VERSION, je.JOB_INSTANCE_ID, je.JOB_CONFIGURATION_LOCATION " +
                                "from batch_job_instance ji, batch_job_execution je, batch_job_execution_params jep " +
                                "where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID " +
                                "and ji.JOB_NAME = ? and (STATUS = ? or STATUS = ?  or STATUS = ? or STATUS = ?) " +
                                "order by je.JOB_EXECUTION_ID desc"
                ),
                mapper,
                parJobName, "STARTED", "STARTING", "STOPPED", "STOPPING");
    }

    public List<JobExecution> getRunningJobExecutionsWithParams(final String parJobName, final JobParameters jobParameters) {

        final JobExecutionRowMapper mapper = new JobExecutionRowMapper(jdbcOperations);
        return jdbcOperations.query(
                this.getQuery(
                        "SELECT je.JOB_EXECUTION_ID, je.START_TIME, je.END_TIME, je.STATUS, je.EXIT_CODE, je.EXIT_MESSAGE, je.CREATE_TIME, je.LAST_UPDATED, je.VERSION, je.JOB_INSTANCE_ID, je.JOB_CONFIGURATION_LOCATION, ji.JOB_NAME, ji.VERSION " +
                                "from batch_job_instance ji, batch_job_execution je, batch_job_execution_params jep " +
                                "where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID " +
                                "and ji.JOB_NAME = ? and EXIT_CODE != ? and STRING_VAL = ? " +
                                "order by je.JOB_EXECUTION_ID desc"
                ),
                mapper,
                parJobName, "COMPLETED", jobParameters.getString("value"));
    }

    public List<JobInfoDB> getInfosRunningJobExecutions(final String parJobName) {
        log.info("[CustomSimpleJobLauncher] ....... get Job running");

        final RowMapper<JobInfoDB> locRowMapper = new JobInstanceRowMapper();
        return jdbcOperations.query(
                this.getQuery(
                        "SELECT je.JOB_EXECUTION_ID " +
                                "from %PREFIX%JOB_INSTANCE ji, %PREFIX%JOB_EXECUTION je, %PREFIX%JOB_EXECUTION_PARAMS jep " +
                                "where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID " +
                                "and ji.JOB_NAME = ? and (STATUS = ? or STATUS = ?  or STATUS = ? or STATUS = ?) and jep.KEY_NAME = ? " +
                                "GROUP BY ji.je.JOB_EXECUTION_ID"
                ),
                locRowMapper,
                parJobName, "STARTED", "STARTING", "STOPPED", "STOPPING", "value");
    }

    public List<JobInfoDB> getInfodRunningJobExecutionsWithParams(final String parJobName, final JobParameters jobParameters) {
        log.info("[CustomSimpleJobLauncher] ....... get Job running");

        final RowMapper<JobInfoDB> locRowMapper = new JobInstanceRowMapper();
        return jdbcOperations.query(
                this.getQuery(
                        "SELECT ji.JOB_INSTANCE_ID, ji.JOB_NAME, je.STATUS, jep.KEY_NAME, jep.STRING_VAL " +
                                "from %PREFIX%JOB_INSTANCE ji, %PREFIX%JOB_EXECUTION je, %PREFIX%JOB_EXECUTION_PARAMS jep " +
                                "where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID " +
                                "and ji.JOB_NAME = ? and (STATUS = ? or STATUS = ?  or STATUS = ? or STATUS = ?) and STRING_VAL = ?" +
                                "GROUP BY ji.JOB_INSTANCE_ID"
                ),
                locRowMapper,
                parJobName, "STARTED", "STARTING", "STOPPED", "STOPPING", jobParameters.getString("value"));
    }

    @NoArgsConstructor
    private static final class JobInstanceRowMapper implements RowMapper<JobInfoDB> {
        @SneakyThrows
        public JobInfoDB mapRow(final ResultSet rs, final int rowNum) throws SQLException {
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
    private final class JobExecutionRowMapper implements RowMapper<JobExecution> {
        private JdbcOperations jdbcOperations;
        private JobInstance jobInstance;
        private JobParameters jobParameters;

        public JobExecutionRowMapper(JdbcOperations jdbcOperations) {
            this.jdbcOperations = jdbcOperations;
            setJdbcTemplate(jdbcOperations);
        }

        public JobExecution mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long id = rs.getLong(1);

            // get all JobExecution Dependencies
            if (null == jobParameters) {
                // TODO - to be change with custom construction of JobParameters object (like jobInstance)
                this.jobParameters = getJobParameters(id);
            }
            if (null == jobInstance) {
                // 11 12 13  (jobInstance informations)
                jobInstance = jobInstanceMapRow(rs);
            }
            // get executionContext if need !

            final String jobConfigurationLocation = rs.getString(11);

            final JobExecution jobExecution = new JobExecution(this.jobInstance, id, this.jobParameters, jobConfigurationLocation);
            jobExecution.setStartTime(rs.getTimestamp(2));
            jobExecution.setEndTime(rs.getTimestamp(3));
            jobExecution.setStatus(BatchStatus.valueOf(rs.getString(4)));
            jobExecution.setExitStatus(new ExitStatus(rs.getString(5), rs.getString(6)));
            jobExecution.setCreateTime(rs.getTimestamp(7));
            jobExecution.setLastUpdated(rs.getTimestamp(8));
            jobExecution.setVersion(rs.getInt(9));
            return jobExecution;
        }

        private JobInstance jobInstanceMapRow(ResultSet rs) throws SQLException {
            final JobInstance jobInstance = new JobInstance(rs.getLong(10), rs.getString(12));
            jobInstance.setVersion(rs.getInt(13));
            //jobInstance.incrementVersion();

            return jobInstance;
        }

    }


}
