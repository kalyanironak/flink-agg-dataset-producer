package com.hack.dataset.generator;

import com.hack.dataset.entities.DataRecord;
import com.hack.dataset.entities.VideoView;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class VideoViewRecordGeneratorTest {

    @Test
    void testGenerateRecordProducesValidFields() {
        VideoViewRecordGenerator generator = new VideoViewRecordGenerator();
        long before = System.currentTimeMillis();
        DataRecord record = generator.generateRecord();
        long after = System.currentTimeMillis();

        assertNotNull(record);
        assertTrue(record instanceof VideoView);
        VideoView vv = (VideoView) record;

        // userId should be a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(vv.getUserId()));
        // videoId in [0,100)
        assertTrue(vv.getVideoId() >= 0 && vv.getVideoId() < 100, "videoId out of range");
        // timestamp within ±5000ms of 'now' (allowing drift between before and after captures)
        long lowerBound = before - 5000;
        long upperBound = after + 5000;
        assertTrue(vv.getTs() >= lowerBound && vv.getTs() <= upperBound,
                "timestamp outside expected ±5000ms window");
    }

    @Test
    void testSuccessiveGenerationsProduceDifferentUserIds() {
        VideoViewRecordGenerator generator = new VideoViewRecordGenerator();
        VideoView first = (VideoView) generator.generateRecord();
        VideoView second = (VideoView) generator.generateRecord();
        assertNotEquals(first.getUserId(), second.getUserId(), "Successive userIds should differ");
    }
}

