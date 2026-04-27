package com.hack.dataset;

import com.hack.dataset.config.ProducerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProducerProperties.class)
public class DatasetProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasetProducerApplication.class, args);
    }
}
