package com.jump.jobs.configs;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Contient les requetes custom
 */
@RequiredArgsConstructor
@Slf4j
public class CustomJdbcJobExecutionDao extends JdbcJobExecutionDao {
    private final JdbcOperations jdbcOperations;

    public List<JobExecution> getRunningJobExecutionsWithParams(final Long parJobExecutionId, final String parJobName, final JobParameters parJobParameters) {
        final JobExecutionRowMapper locMapper = new JobExecutionRowMapper(jdbcOperations);
        return jdbcOperations.query(
                this.getQuery(
                        "SELECT je.JOB_EXECUTION_ID, je.START_TIME, je.END_TIME, je.STATUS, je.EXIT_CODE, je.EXIT_MESSAGE, je.CREATE_TIME, je.LAST_UPDATED, je.VERSION, je.JOB_INSTANCE_ID, je.JOB_CONFIGURATION_LOCATION, ji.JOB_NAME, ji.VERSION " +
                                "from batch_job_instance ji, batch_job_execution je, batch_job_execution_params jep " +
                                "where je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID and ji.JOB_INSTANCE_ID = je.JOB_INSTANCE_ID " +
                                "and je.JOB_EXECUTION_ID != ? and ji.JOB_NAME = ? and EXIT_CODE != ? and EXIT_CODE != ? and STRING_VAL = ? " +
                                "order by je.JOB_EXECUTION_ID desc"
                ),
                locMapper,
                parJobExecutionId, parJobName, "COMPLETED", "NOOP", parJobParameters.getString("value")
        );
    }

    @NoArgsConstructor
    private final class JobExecutionRowMapper implements RowMapper<JobExecution> {

        public JobExecutionRowMapper(final JdbcOperations parJdbcOperations) {
            setJdbcTemplate(parJdbcOperations);
        }

        public JobExecution mapRow(final ResultSet parResultSet, final int parRowNum) throws SQLException {
            final Long id = parResultSet.getLong(1);

            // get all JobExecution Dependencies
            final JobParameters locJobParameters = getJobParameters(id);
            final JobInstance locJobInstance = jobInstanceMapRow(parResultSet);
            // can get executionContext if need !

            final String locJobConfigurationLocation = parResultSet.getString(11);

            final JobExecution locJobExecution = new JobExecution(locJobInstance, id, locJobParameters, locJobConfigurationLocation);
            locJobExecution.setStartTime(parResultSet.getTimestamp(2));
            locJobExecution.setEndTime(parResultSet.getTimestamp(3));
            locJobExecution.setStatus(BatchStatus.valueOf(parResultSet.getString(4)));
            locJobExecution.setExitStatus(new ExitStatus(parResultSet.getString(5), parResultSet.getString(6)));
            locJobExecution.setCreateTime(parResultSet.getTimestamp(7));
            locJobExecution.setLastUpdated(parResultSet.getTimestamp(8));
            locJobExecution.setVersion(parResultSet.getInt(9));
            return locJobExecution;
        }

        private JobInstance jobInstanceMapRow(final ResultSet parResultSet) throws SQLException {
            final JobInstance locJobInstance = new JobInstance(parResultSet.getLong(10), parResultSet.getString(12));
            locJobInstance.setVersion(parResultSet.getInt(13));
            return locJobInstance;
        }

    }


}
