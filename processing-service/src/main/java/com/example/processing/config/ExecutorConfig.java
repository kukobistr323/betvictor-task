package com.example.processing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService(HipsumClientProperties hipsumProps) {
        return Executors.newFixedThreadPool(hipsumProps.getParallelism());
    }
}
