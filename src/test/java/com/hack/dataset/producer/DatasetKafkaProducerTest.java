package com.hack.dataset.producer;

import com.hack.dataset.config.ProducerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class DatasetKafkaProducerTest {

    @Test
    void testSendDelegatesToKafkaTemplate() {
        KafkaTemplate<String,String> kafkaTemplate = mock(KafkaTemplate.class);
        ProducerProperties props = new ProducerProperties();
        props.setTopic("test-topic");
        props.setDryRun(false);

        DatasetKafkaProducer producer = new DatasetKafkaProducer(kafkaTemplate, props);
        producer.send("user-1", "{\"k\":1}");

        // Verify send invoked with topic, key, value
        verify(kafkaTemplate, times(1)).send(eq("test-topic"), eq("user-1"), eq("{\"k\":1}"));
    }
}
