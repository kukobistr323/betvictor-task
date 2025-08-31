package com.example.repo.controller;

import com.example.repo.model.HistoryRecord;
import com.example.repo.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private HistoryRepository repository;

    @InjectMocks
    private HistoryController controller;

    @Test
    void history_returnsSnakeCaseJson_withLatest10() throws Exception {
        // given: 2 items (you can mock 10+ if you want)
        var now = OffsetDateTime.now();
        var e1 = HistoryRecord.builder()
                .id(UUID.randomUUID())
                .freqWord("hipster")
                .avgParagraphSize(10.0)
                .avgParagraphProcessingTime(5.0)
                .totalProcessingTime(40.0)
                .createdAt(now)
                .build();

        var e2 = HistoryRecord.builder()
                .id(UUID.randomUUID())
                .freqWord("coffee")
                .avgParagraphSize(7.0)
                .avgParagraphProcessingTime(3.0)
                .totalProcessingTime(25.0)
                .createdAt(now.minusMinutes(1))
                .build();

        when(repository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of(e1, e2));

        // when
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        // then: verify snake_case in JSON (Jackson @JsonNaming on DTO)
        mvc.perform(get("/betvictor/history").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].freq_word").value("hipster"))
                .andExpect(jsonPath("$[0].avg_paragraph_size").value(10.0))
                .andExpect(jsonPath("$[0].avg_paragraph_processing_time").value(5.0))
                .andExpect(jsonPath("$[0].total_processing_time").value(40.0))
                .andExpect(jsonPath("$[1].freq_word").value("coffee"));

        verify(repository, times(1)).findTop10ByOrderByCreatedAtDesc();
        verifyNoMoreInteractions(repository);
    }
}
