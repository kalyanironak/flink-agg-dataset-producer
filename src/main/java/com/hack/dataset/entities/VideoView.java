package com.hack.dataset.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoView implements DataRecord {

    private String userId;
    private long videoId;
    private long ts;

    @Override
    public String getKey() {
        return userId;
    }

}
