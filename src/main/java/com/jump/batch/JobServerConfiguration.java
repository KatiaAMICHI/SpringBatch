package com.jump.batch;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class JobServerConfiguration {
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    @Primary
    public DataSource dataSource() {
        final DataSourceProperties locDataSourceProperties = new DataSourceProperties();
        locDataSourceProperties.setUrl(url);
        locDataSourceProperties.setUsername(username);
        locDataSourceProperties.setPassword(password);
        final HikariDataSource locBuild = locDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        //all
        locBuild.addDataSourceProperty("zeroDateTimeBehavior", "CONVERT_TO_NULL");
        locBuild.addDataSourceProperty("serverTimezone", "UTC");
        locBuild.addDataSourceProperty("characterEncoding", "utf8");
        locBuild.addDataSourceProperty("useUnicode", true);
        return locBuild;
    }

    /*@Bean
    public JobConfigurationRepository jobConfigRepo() {
        return new JobConfigurationRepository() {
            @Override
            public JobConfiguration getJobConfiguration(Long aLong, String s) throws NoSuchJobConfigurationException {
                return null;
            }

            @Override
            public Collection<JobConfiguration> getJobConfigurations(String s, String s1) throws NoSuchJobException, NoSuchJobConfigurationException {
                return null;
            }

            @Override
            public JobConfiguration add(JobConfiguration jobConfiguration, String s) {
                return null;
            }

            @Override
            public JobConfiguration update(JobConfiguration jobConfiguration, String s) throws NoSuchJobConfigurationException {
                return null;
            }

            @Override
            public void delete(JobConfiguration jobConfiguration, String s) throws NoSuchJobConfigurationException {

            }

            @Override
            public Collection<JobConfiguration> getAllJobConfigurations(String s) {
                return null;
            }

            @Override
            public Collection<JobConfiguration> getAllJobConfigurationsByJobNames(Collection<String> collection, String s) {
                return null;
            }
        };
    }*/
}