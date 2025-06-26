package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.AnalysisHistory;

import java.util.List;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Integer> {
    List<AnalysisHistory> findByUserIdOrderByCreatedAtDesc(int userId);
}
