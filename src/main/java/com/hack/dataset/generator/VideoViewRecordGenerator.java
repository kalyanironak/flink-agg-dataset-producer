package com.hack.dataset.generator;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.hack.dataset.entities.DataRecord;
import com.hack.dataset.entities.VideoView;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(prefix = "producer", name = "dataset", havingValue = "video-views", matchIfMissing = true)
public final class VideoViewRecordGenerator implements DataRecordGenerator {

    public DataRecord generateRecord(){

        String userId = UUID.randomUUID().toString();
        int videoId = ThreadLocalRandom.current().nextInt(20);
        long timestamp = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(-5000, 5001);

        VideoView videoView = new VideoView(userId, videoId, timestamp);
        return videoView;
    }

}

