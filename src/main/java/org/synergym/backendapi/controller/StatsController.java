// src/main/java/org/synergym/backendapi/controller/StatsController.java
package org.synergym.backendapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.synergym.backendapi.dto.ComparisonStatsDTO;
import org.synergym.backendapi.service.StatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/comparison/{userId}")
    public ResponseEntity<ComparisonStatsDTO> getComparisonStats(@PathVariable Integer userId) {
        ComparisonStatsDTO stats = statsService.getComparisonStats(userId);
        return ResponseEntity.ok(stats);
    }
}