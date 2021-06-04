package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

/**
 * Configuration coustom de l'instanceIndex pour chaque instance du worker-service
 */
@Component
@Slf4j
class ServerPortCustomize implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Value("${port.number.min}")
    private Integer minPortNum;
    @Value("${port.number.max}")
    private Integer maxPortNum;
    @Value("${spring.cloud.stream.instanceCount}")
    private Integer instanceCount;

    @Override
    public void customize(final ConfigurableWebServerFactory parFactory) {
        final int locPort = SocketUtils.findAvailableTcpPort(minPortNum, maxPortNum);
        parFactory.setPort(locPort);
        System.getProperties().put("server.port", locPort);
        System.getProperties().put("spring.cloud.stream.instanceIndex", locPort-minPortNum);
    }
}