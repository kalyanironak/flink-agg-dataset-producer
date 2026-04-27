package com.hack.dataset.producer;

public interface DatasetProducerClient {
    void send(String key, String value);
}

