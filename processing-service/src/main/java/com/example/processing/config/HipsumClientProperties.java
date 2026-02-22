package com.example.processing.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.hipsum")
public class HipsumClientProperties {

    @NotBlank
    private String baseUrl;
    @NotBlank
    private String type = "hipster-centric";
    @Min(1)
    private int parallelism = 8;
    @Min(100)
    private int connectTimeoutMs = 3000;
    @Min(100)
    private int readTimeoutMs = 8000;
    @Min(0)
    private int retries = 1;
    @Min(100)
    private int overallTimeoutMs = 20000;
}