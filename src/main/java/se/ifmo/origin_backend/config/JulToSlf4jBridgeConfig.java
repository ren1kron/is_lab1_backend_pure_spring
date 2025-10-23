package se.ifmo.origin_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.slf4j.bridge.SLF4JBridgeHandler;

@Configuration
public class JulToSlf4jBridgeConfig {
    @PostConstruct
    public void init() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
