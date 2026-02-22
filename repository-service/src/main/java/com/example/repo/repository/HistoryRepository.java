package com.example.repo.repository;

import com.example.repo.model.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryRecord, UUID> {
    List<HistoryRecord> findTop10ByOrderByCreatedAtDesc();
}
