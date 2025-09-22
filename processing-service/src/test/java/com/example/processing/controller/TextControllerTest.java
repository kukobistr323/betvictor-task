package com.example.processing.controller;

import com.example.processing.model.TextResponse;
import com.example.processing.service.TextOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TextController.class)
class TextControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private TextOrchestrator orchestrator;

    @Test
    void getText_returns400_whenParamMissingOrInvalid() {
        // missing param -> 400
        webTestClient.get()
                .uri("/counter/text")
                .exchange()
                .expectStatus().isBadRequest();

        // p=0 violates @Min(1) -> 400
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/counter/text").queryParam("p", "0").build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getText_returns200_andBody_whenValid() {
        when(orchestrator.process(anyInt())).thenReturn(
                TextResponse.builder()
                        .freqWord("hipster")
                        .avgParagraphSize(5.0)
                        .avgParagraphProcessingTime(7.0)
                        .totalProcessingTime(42)
                        .build()
        );

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/counter/text").queryParam("p", "3").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.freq_word").isEqualTo("hipster")
                .jsonPath("$.total_processing_time").isEqualTo(42);
    }
}
