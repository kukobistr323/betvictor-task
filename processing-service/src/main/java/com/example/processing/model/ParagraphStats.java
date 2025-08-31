package com.example.processing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphStats {
    int wordCount;
    long analysisMs;
    @Singular("count")
    Map<String, Integer> frequency;
}