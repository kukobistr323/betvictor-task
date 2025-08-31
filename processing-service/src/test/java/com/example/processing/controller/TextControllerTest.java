package com.example.processing.controller;

import com.example.processing.model.TextResponse;
import com.example.processing.service.TextOrchestrator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TextControllerTest {

    @Mock
    private TextOrchestrator orchestrator;
    @InjectMocks
    private TextController controller;

    @Test
    void getText_returns400_whenParamMissingOrInvalid() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/betvictor/text"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/betvictor/text").param("p", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getText_returns200_andBody_whenValid() throws Exception {
        when(orchestrator.process(anyInt())).thenReturn(TextResponse.builder()
                .freqWord("hipster")
                .avgParagraphSize(5.0)
                .avgParagraphProcessingTime(7.0)
                .totalProcessingTime(42)
                .build());

        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/betvictor/text").param("p", "3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.freq_word").value("hipster"))
                .andExpect(jsonPath("$.total_processing_time").value(42));
    }
}
