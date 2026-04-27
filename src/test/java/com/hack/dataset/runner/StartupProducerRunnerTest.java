package com.hack.dataset.runner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hack.dataset.entities.DataRecord;
import com.hack.dataset.entities.VideoView;
import com.hack.dataset.generator.DataRecordGenerator;
import com.hack.dataset.producer.DatasetProducerClient;
import com.hack.dataset.config.ProducerProperties;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StartupProducerRunnerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSerializeVideoViewProducesExpectedJson() throws Exception {
        DataRecord record = new VideoView("user-1", 42L, 123456789L);
        String json = mapper.writeValueAsString(record);
        JsonNode node = mapper.readTree(json);
        assertEquals("user-1", node.get("userId").asText());
        assertEquals(42L, node.get("videoId").asLong());
        assertEquals(123456789L, node.get("ts").asLong());
        // Ensure no unexpected extra fields
        assertEquals(3, node.size());
    }

    @Test
    void testRunnerSendsConfiguredNumberOfRecords() throws Exception {
        // Prepare 3 deterministic records
        List<VideoView> views = List.of(
                new VideoView("user-A", 1L, 1000L),
                new VideoView("user-B", 2L, 2000L),
                new VideoView("user-C", 3L, 3000L)
        );
        StubGenerator generator = new StubGenerator(views);
        CapturingProducer producer = new CapturingProducer();
        ProducerProperties props = new ProducerProperties();
        props.setTopic("dummy-topic");
        props.setCount(views.size());
        props.setSleepMs(0L);
        props.setDryRun(true);

        StartupProducerRunner runner = new StartupProducerRunner(generator, producer, props);
        runner.run();

        assertEquals(views.size(), producer.sent.size(), "Unexpected number of sends");
        for (int i = 0; i < views.size(); i++) {
            VideoView expected = views.get(i);
            SentRecord actual = producer.sent.get(i);
            assertEquals(expected.getUserId(), actual.key, "Key should be userId");
            JsonNode node = mapper.readTree(actual.valueJson);
            assertEquals(expected.getUserId(), node.get("userId").asText());
            assertEquals(expected.getVideoId(), node.get("videoId").asLong());
            assertEquals(expected.getTs(), node.get("ts").asLong());
            assertEquals(3, node.size());
        }
    }

    @Test
    void testRunnerWithZeroCountDoesNotSendAnything() throws Exception {
        StubGenerator generator = new StubGenerator(Collections.emptyList());
        CapturingProducer producer = new CapturingProducer();
        ProducerProperties props = new ProducerProperties();
        props.setTopic("dummy-topic");
        props.setCount(0);
        props.setSleepMs(0L);
        props.setDryRun(true);

        StartupProducerRunner runner = new StartupProducerRunner(generator, producer, props);
        runner.run();
        assertTrue(producer.sent.isEmpty(), "No messages should have been sent when count=0");
    }

    // --- Helpers -----------------------------------------------------------------
    private static class StubGenerator implements DataRecordGenerator {
        private final List<VideoView> source;
        private int idx = 0;
        StubGenerator(List<VideoView> source) { this.source = source; }
        @Override public DataRecord generateRecord() { return source.get(idx++); }
    }

    private static class CapturingProducer implements DatasetProducerClient {
        private final List<SentRecord> sent = new ArrayList<>();
        @Override public void send(String key, String value) { sent.add(new SentRecord(key, value)); }
    }

    private record SentRecord(String key, String valueJson) {}
}
