package com.example.processing.service;

import com.example.processing.model.ParagraphStats;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextAnalyzerTest {

    private final TextAnalyzer analyzer = new TextAnalyzer();

    @Test
    void analyze_countsWords_andBuildsFrequency() {
        String paragraph = "Hipster coffee coffee — and well-crafted code!";
        ParagraphStats stats = analyzer.analyze(1, paragraph);

        assertThat(stats.getWordCount()).isEqualTo(6); // hipster, coffee, coffee, and, well-crafted, code
        assertThat(stats.getFrequency())
                .containsEntry("coffee", 2)
                .containsEntry("hipster", 1)
                .containsEntry("well-crafted", 1);
        assertThat(stats.getAnalysisMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void analyze_handlesHyphenatedTokens() {
        String paragraph = "State-of-the-art hipster-friendly tools";
        ParagraphStats stats = analyzer.analyze(2, paragraph);

        assertThat(stats.getWordCount()).isEqualTo(3); // state-of-the-art, hipster-friendly, tools
        assertThat(stats.getFrequency())
                .containsEntry("state-of-the-art", 1)
                .containsEntry("hipster-friendly", 1)
                .containsEntry("tools", 1);
    }
}
