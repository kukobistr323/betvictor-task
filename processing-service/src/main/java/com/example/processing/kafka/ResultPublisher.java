package com.example.processing.kafka;

import com.example.processing.config.KafkaProperties;
import com.example.processing.model.TextResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publish(TextResponse response) {
        String key = response.getFreqWord();
        kafkaTemplate.send(kafkaProperties.getTopic(), key, response);
        log.info("Published to Kafka topic={} key={}", kafkaProperties.getTopic(), key);
    }
}
