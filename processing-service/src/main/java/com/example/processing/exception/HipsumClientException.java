package com.example.processing.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HipsumClientException extends RuntimeException {

    private String message;
    private Throwable cause;
}
