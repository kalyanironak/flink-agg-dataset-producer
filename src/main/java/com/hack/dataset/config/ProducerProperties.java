package com.hack.dataset.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "producer")
public class ProducerProperties {
    private String topic ;
    private int count ;
    private long sleepMs ;
    private boolean dryRun;
}

