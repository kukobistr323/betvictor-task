package com.example.processing.service;

import com.example.processing.client.HipsumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParagraphFetcherTest {

    @Mock
    private HipsumClient client;
    @InjectMocks
    private ParagraphFetcher fetcher;

    @Test
    void fetchOne_delegatesToClient() {
        when(client.fetchParagraph()).thenReturn("hello world");

        String out = fetcher.fetchOne(1);

        assertThat(out).isEqualTo("hello world");
        verify(client, times(1)).fetchParagraph();
    }
}
