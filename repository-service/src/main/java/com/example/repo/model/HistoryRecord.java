package com.example.repo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "freq_word", nullable = false)
    private String freqWord;

    @Column(name = "avg_paragraph_size", nullable = false)
    private double avgParagraphSize;

    @Column(name = "avg_paragraph_processing_time", nullable = false)
    private double avgParagraphProcessingTime;

    @Column(name = "total_processing_time", nullable = false)
    private double totalProcessingTime;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
