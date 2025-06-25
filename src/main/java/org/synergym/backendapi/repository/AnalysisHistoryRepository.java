package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.AnalysisHistory;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Integer> {
}
