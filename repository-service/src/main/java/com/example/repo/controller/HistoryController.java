package com.example.repo.controller;

import com.example.repo.model.TextResponse;
import com.example.repo.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/counter")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryRepository repository;

    @GetMapping("/history")
    public List<TextResponse> history() {
        var list = repository.findTop10ByOrderByCreatedAtDesc();
        var result = list.stream()
                .map(e -> TextResponse.builder()
                        .freqWord(e.getFreqWord())
                        .avgParagraphSize(e.getAvgParagraphSize())
                        .avgParagraphProcessingTime(e.getAvgParagraphProcessingTime())
                        .totalProcessingTime(e.getTotalProcessingTime())
                        .build())
                .toList();

        log.info("Returning {} history items", result.size());
        return result;
    }
}
