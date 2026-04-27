package com.hack.dataset.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hack.dataset.config.ProducerProperties;
import com.hack.dataset.entities.DataRecord;
import com.hack.dataset.generator.DataRecordGenerator;
import com.hack.dataset.producer.DatasetProducerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupProducerRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DataRecordGenerator dataRecordGenerator;
    private final DatasetProducerClient producerClient;
    private final ProducerProperties properties;

    @Override
    public void run(String... args) throws Exception {
        int count = properties.getCount();
        long sleepMs = properties.getSleepMs();

        for (int i = 0; i < count; i++) {

            DataRecord kafkaRecord = dataRecordGenerator.generateRecord();
            producerClient.send(kafkaRecord.getKey(), serializeRecord(kafkaRecord));
            if (sleepMs > 0) Thread.sleep(sleepMs);
        }
    }

    private String serializeRecord(DataRecord record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
