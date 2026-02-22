package com.example.processing.service;

import com.example.processing.kafka.ResultPublisher;
import com.example.processing.model.ParagraphStats;
import com.example.processing.model.TextResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextOrchestrator {

    private final ParagraphFetcher fetcher;
    private final TextAnalyzer analyzer;
    private final AggregationService aggregator;
    private final ResultPublisher publisher;
    private final ExecutorService analysisExecutor; // provided by your ExecutorConfig

    public TextResponse process(int p) {
        log.info("Processing request with p={}", p);
        Instant t0 = Instant.now();

        // submit p parallel tasks
        List<CompletableFuture<ParagraphStats>> futures = IntStream.range(0, p)
                .mapToObj(idx ->
                        CompletableFuture.supplyAsync(() -> {
                            // fetch → analyze inside the worker thread
                            String paragraph = fetcher.fetchOne(idx);
                            return analyzer.analyze(idx, paragraph);
                        }, analysisExecutor))
                .toList();

        // wait for all tasks
        List<ParagraphStats> stats = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .join();

        long totalMs = Duration.between(t0, Instant.now()).toMillis();
        TextResponse response = aggregator.aggregate(stats, totalMs);

        log.info("Publishing result to Kafka: {}", response);
        publisher.publish(response);

        return response;
    }
}
