package com.example.processing.client;

import com.example.processing.config.HipsumClientProperties;
import com.example.processing.exception.HipsumClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class HipsumClient {

    private static final int PARAS_VALUE = 1;
    private static final int MIN_BACKOFF_MILLIS = 300;
    private static final int MAX_BACKOFF_SECONDS = 3;
    private static final long DEFAULT_TIMEOUT_ADD = 1000L;

    private final WebClient hipsumWebClient;
    private final HipsumClientProperties props;

    public String fetchParagraph() {
        log.info("Fetching data from Hipsum API: type={}, paras={}", props.getType(), PARAS_VALUE);
        try {
            String[] arr = hipsumWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/")
                            .queryParam("type", props.getType())
                            .queryParam("paras", PARAS_VALUE)
                            .build())
                    .retrieve()
                    .bodyToMono(String[].class)
                    .timeout(Duration.ofMillis(props.getOverallTimeoutMs()))
                    .retryWhen(
                            Retry.backoff(props.getRetries(), Duration.ofMillis(MIN_BACKOFF_MILLIS))
                                    .maxBackoff(Duration.ofSeconds(MAX_BACKOFF_SECONDS))
                                    .filter(this::isRetryable)
                    )
                    .block(Duration.ofMillis(props.getOverallTimeoutMs() + DEFAULT_TIMEOUT_ADD));

            if (arr == null || arr.length == 0) {
                log.warn("Hipsum returned empty payload");
                return "";
            }

            return Arrays.stream(arr).findFirst().orElse("");
        } catch (WebClientResponseException ex) {
            log.error("Hipsum error: status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new HipsumClientException("Hipsum HTTP " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("Hipsum call failed", ex);
            throw new HipsumClientException("Failed to call Hipsum", ex);
        }
    }

    private boolean isRetryable(Throwable t) {
        if (t instanceof WebClientResponseException exception) {
            return exception.getStatusCode().is5xxServerError();
        }
        return true;
    }
}
