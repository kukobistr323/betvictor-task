package com.example.processing.controller;

import com.example.processing.model.TextResponse;
import com.example.processing.service.TextOrchestrator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/counter")
@RequiredArgsConstructor
public class TextController {

    private final TextOrchestrator orchestrator;

    @GetMapping("/text")
    public ResponseEntity<TextResponse> getText(@RequestParam("p") @NotNull @Min(1) Integer p) {
        return ResponseEntity.ok(orchestrator.process(p));
    }
}
