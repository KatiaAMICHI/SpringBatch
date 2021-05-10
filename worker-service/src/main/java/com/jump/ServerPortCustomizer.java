package com.jump;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

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
    public void customize(final ConfigurableWebServerFactory factory) {
        final int port = SocketUtils.findAvailableTcpPort(minPortNum, maxPortNum);
        factory.setPort(port);
        System.getProperties().put("server.port", port);
        System.getProperties().put("spring.cloud.stream.instanceIndex", port-minPortNum);
    }
}