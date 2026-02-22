package com.example.processing.service;

import com.example.processing.model.ParagraphStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TextAnalyzer {

    private static final Pattern TOKEN = Pattern.compile("\\p{L}+(?:[\\p{Pd}]\\p{L}+)*");

    public ParagraphStats analyze(int idx, String paragraph) {
        Instant start = Instant.now();

        Map<String, Integer> freq = new HashMap<>();
        int words = 0;

        var m = TOKEN.matcher(paragraph.toLowerCase(Locale.ROOT));
        while (m.find()) {
            words++;
            freq.merge(m.group(), 1, Integer::sum);
        }

        long elapsed = Duration.between(start, Instant.now()).toMillis();
        log.info("Paragraph #{} analyzed: wordCount={}, analyzeMs={}ms", idx, words, elapsed);

        return ParagraphStats.builder()
                .wordCount(words)
                .analysisMs(elapsed)
                .frequency(freq)
                .build();
    }
}
