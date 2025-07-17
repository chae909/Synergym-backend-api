// src/main/java/org/synergym/backendapi/service/StatsService.java
package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.ComparisonStatsDTO;

public interface StatsService {
    ComparisonStatsDTO getComparisonStats(Integer userId);
}