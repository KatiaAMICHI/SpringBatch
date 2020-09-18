package com.jump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.tuxdevelop.spring.batch.lightmin.server.annotation.EnableLightminServer;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
@EnableLightminServer
public class LightminServerSimple {

    public static void main(final String[] args) {
        SpringApplication.run(LightminServerSimple.class, args);
    }
}
