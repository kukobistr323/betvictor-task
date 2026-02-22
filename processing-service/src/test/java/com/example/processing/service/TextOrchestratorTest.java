package com.example.processing.service;

import com.example.processing.kafka.ResultPublisher;
import com.example.processing.model.ParagraphStats;
import com.example.processing.model.TextResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextOrchestratorTest {

    @Mock
    ParagraphFetcher fetcher;
    @Mock
    TextAnalyzer analyzer;
    @Mock
    AggregationService aggregator;
    @Mock
    ResultPublisher publisher;

    ExecutorService pool;
    TextOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        pool = Executors.newFixedThreadPool(4);
        orchestrator = new TextOrchestrator(fetcher, analyzer, aggregator, publisher, pool);
    }

    @AfterEach
    void tearDown() {
        pool.shutdownNow();
    }

    @Test
    void process_runsInParallel_aggregatesAndPublishes() {
        int p = 3;

        // fetcher returns some text per call
        when(fetcher.fetchOne(anyInt()))
                .thenAnswer(inv -> "para-" + inv.getArgument(0));

        // analyzer returns stats per paragraph
        when(analyzer.analyze(anyInt(), anyString()))
                .thenAnswer(inv -> ParagraphStats.builder()
                        .wordCount(5)
                        .analysisMs(7)
                        .frequency(Map.of("hipster", 1))
                        .build());

        // aggregator returns a fixed response
        TextResponse aggregated = TextResponse.builder()
                .freqWord("hipster")
                .avgParagraphSize(5.0)
                .avgParagraphProcessingTime(7.0)
                .totalProcessingTime(42)
                .build();
        when(aggregator.aggregate(anyList(), anyLong())).thenReturn(aggregated);

        // act
        TextResponse out = orchestrator.process(p);

        // assert
        assertThat(out.getFreqWord()).isEqualTo("hipster");
        verify(fetcher, times(p)).fetchOne(anyInt());
        verify(analyzer, times(p)).analyze(anyInt(), anyString());
        verify(aggregator, times(1)).aggregate(anyList(), anyLong());
        verify(publisher, times(1)).publish(aggregated);
        verifyNoMoreInteractions(publisher);
    }
}
