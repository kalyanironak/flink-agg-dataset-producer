package com.hack.dataset.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "producer", name = "dry-run", havingValue = "true")
public class DryRunDatasetProducer implements DatasetProducerClient {
    @Override
    public void send(String key, String value) {
        System.out.printf("key=%s value=%s%n", key, value);
    }
}

