package com.example.processing.service;

import com.example.processing.model.ParagraphStats;
import com.example.processing.model.TextResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AggregationServiceTest {

    private final AggregationService aggregation = new AggregationService();

    @Test
    void aggregate_mergesFrequencies_andComputesAverages_andUsesAlphaTieBreak() {
        // p1: apple=1, banana=1
        var p1 = ParagraphStats.builder()
                .wordCount(5).analysisMs(10)
                .frequency(Map.of("apple", 1, "banana", 1))
                .build();

        // p2: banana=1, avocado=2  -> totals: banana=2, avocado=2, apple=1
        var p2 = ParagraphStats.builder()
                .wordCount(7).analysisMs(20)
                .frequency(Map.of("banana", 1, "avocado", 2))
                .build();

        long totalMs = 123;
        TextResponse resp = aggregation.aggregate(List.of(p1, p2), totalMs);

        assertThat(resp.getAvgParagraphSize()).isEqualTo((5 + 7) / 2.0);
        assertThat(resp.getAvgParagraphProcessingTime()).isEqualTo((10 + 20) / 2.0);
        assertThat(resp.getTotalProcessingTime()).isEqualTo(123);

        // banana and avocado both = 2; alphabetical tie-break -> "avocado"
        assertThat(resp.getFreqWord()).isEqualTo("avocado");
    }

}
