package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.AnalysisHistoryDTO;
import org.synergym.backendapi.service.AnalysisHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/analysis-histories")
@RequiredArgsConstructor
public class AnalysisHistoryController {

    private final AnalysisHistoryService analysisHistoryService;

    // 특정 사용자의 분석 기록 생성
    @PostMapping("/user/{userId}")
    public ResponseEntity<AnalysisHistoryDTO> createAnalysisHistory(
            @PathVariable int userId,
            @RequestBody AnalysisHistoryDTO requestDTO) {
        AnalysisHistoryDTO createdHistory = analysisHistoryService.createAnalysisHistory(requestDTO, userId);
        return new ResponseEntity<>(createdHistory, HttpStatus.CREATED);
    }

    // Id로 특정 분석 기록 조회
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisHistoryDTO> getAnalysisHistory(@PathVariable int id) {
        AnalysisHistoryDTO historyDTO = analysisHistoryService.getAnalysisHistoryById(id);
        return ResponseEntity.ok(historyDTO);
    }

    // 특정 사용자의 모든 분석기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalysisHistoryDTO>> getAllAnalysisHistoryByUserId(@PathVariable int userId) {
        List<AnalysisHistoryDTO> histories = analysisHistoryService.getAllAnalysisHistoryByUserId(userId);
        return ResponseEntity.ok(histories);
    }

    // 분석기록 수정
    @PutMapping("/{id}")
    public ResponseEntity<AnalysisHistoryDTO> updateAnalysisHistory(
            @PathVariable int id,
            @RequestBody AnalysisHistoryDTO requestDTO) {
        AnalysisHistoryDTO updatedHistory = analysisHistoryService.updateAnalysisHistory(id, requestDTO);
        return ResponseEntity.ok(updatedHistory);
    }

    // 분석기록 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysisHistory(@PathVariable int id) {
        analysisHistoryService.deleteAnalysisHistory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
