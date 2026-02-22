package com.example.repo.consumer;

import com.example.repo.model.HistoryRecord;
import com.example.repo.model.TextResponse;
import com.example.repo.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ResultListenerTest {

    @Mock
    private HistoryRepository repository;

    @InjectMocks
    private ResultListener listener;

    @Captor
    private ArgumentCaptor<HistoryRecord> recordCaptor;

    @Test
    void onMessage_persistsMappedEntity() {
        // given
        var payload = TextResponse.builder()
                .freqWord("hipster")
                .avgParagraphSize(12.5)
                .avgParagraphProcessingTime(3.2)
                .totalProcessingTime(48.0)
                .build();

        // when: directly invoke the @KafkaListener method
        listener.onMessage(payload, "hipster");

        // then
        verify(repository).save(recordCaptor.capture());
        var saved = recordCaptor.getValue();

        assertThat(saved.getFreqWord()).isEqualTo("hipster");
        assertThat(saved.getAvgParagraphSize()).isEqualTo(12.5);
        assertThat(saved.getAvgParagraphProcessingTime()).isEqualTo(3.2);
        assertThat(saved.getTotalProcessingTime()).isEqualTo(48.0);

        // createdAt should be set (by listener) and be close to 'now'
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    void onMessage_usesKafkaKey_onlyForLogging_noSideEffects() {
        var payload = TextResponse.builder()
                .freqWord("same-key")
                .avgParagraphSize(1.0)
                .avgParagraphProcessingTime(1.0)
                .totalProcessingTime(1.0)
                .build();

        listener.onMessage(payload, "some-key-from-kafka");

        verify(repository, times(1)).save(any(HistoryRecord.class));
        verifyNoMoreInteractions(repository);
    }
}
