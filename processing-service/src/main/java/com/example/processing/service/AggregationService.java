package com.example.processing.service;

import com.example.processing.model.ParagraphStats;
import com.example.processing.model.TextResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AggregationService {

    /**
     * Merge stats and compute final response fields.
     */
    public TextResponse aggregate(List<ParagraphStats> stats, long totalMs) {
        double avgSize = stats.stream().mapToInt(ParagraphStats::getWordCount).average().orElse(0.0);
        double avgParaMs = stats.stream().mapToLong(ParagraphStats::getAnalysisMs).average().orElse(0.0);

        Map<String, Integer> merged = new HashMap<>();
        for (ParagraphStats s : stats) {
            s.getFrequency().forEach((w, c) -> merged.merge(w, c, Integer::sum));
        }

        String freqWord = merged.entrySet().stream()
                .max(Comparator.<Map.Entry<String,Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElse("");

        var response = TextResponse.builder()
                .freqWord(freqWord)
                .avgParagraphSize(avgSize)
                .avgParagraphProcessingTime(avgParaMs)
                .totalProcessingTime(totalMs)
                .build();

        log.info("Aggregation complete: most frequent word='{}', totalMs={}", freqWord, totalMs);
        return response;
    }
}
