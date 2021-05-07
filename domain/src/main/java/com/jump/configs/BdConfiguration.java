package com.jump.configs;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
public class BdConfiguration {
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.url}")
    private String url;

    @Bean
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

    @Bean
    @Primary
    public PlatformTransactionManager dataSourceTransactionManager(final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }
}
