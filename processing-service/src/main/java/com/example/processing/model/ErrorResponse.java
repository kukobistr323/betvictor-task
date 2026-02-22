package com.example.processing.model;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String errorCode,
        String errorMessage
) {
}
