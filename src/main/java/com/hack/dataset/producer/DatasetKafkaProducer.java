package com.hack.dataset.producer;

import com.hack.dataset.config.ProducerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "producer", name = "dry-run", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class DatasetKafkaProducer implements DatasetProducerClient {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProducerProperties props;

    @Override
    public void send(String key, String value) {
        kafkaTemplate.send(props.getTopic(), key, value);
    }
}
