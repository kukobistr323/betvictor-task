package com.example.processing.service;

import com.example.processing.client.HipsumClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParagraphFetcher {

    private final HipsumClient hipsumClient;

    public String fetchOne(int idx) {
        log.info("Fetching data from Hipsum (paragraph #{})", idx);
        String paragraph = hipsumClient.fetchParagraph();
        log.info("Response body is: {}", paragraph);
        return paragraph;
    }
}
