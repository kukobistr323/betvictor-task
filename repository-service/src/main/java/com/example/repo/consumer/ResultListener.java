package com.example.repo.consumer;

import com.example.repo.model.HistoryRecord;
import com.example.repo.model.TextResponse;
import com.example.repo.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultListener {

    private final HistoryRepository repository;

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "repo-service",
            concurrency = "${app.kafka.concurrency:1}"
    )
    public void onMessage(TextResponse payload,
                          @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Consumed message key={} payload={}", key, payload);

        var entity = HistoryRecord.builder()
                .freqWord(payload.getFreqWord())
                .avgParagraphSize(payload.getAvgParagraphSize())
                .avgParagraphProcessingTime(payload.getAvgParagraphProcessingTime())
                .totalProcessingTime(payload.getTotalProcessingTime())
                .createdAt(OffsetDateTime.now())
                .build();

        repository.save(entity);
        log.info("Saved history record id={} word={}", entity.getId(), entity.getFreqWord());
    }
}