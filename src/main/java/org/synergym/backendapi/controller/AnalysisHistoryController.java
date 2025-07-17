package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.AnalysisHistoryDTO;
import org.synergym.backendapi.dto.AnalysisRequestDTO;
import org.synergym.backendapi.service.AnalysisHistoryService;
import org.synergym.backendapi.service.PostureGraphClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis-histories")
@RequiredArgsConstructor
public class AnalysisHistoryController {

    private final AnalysisHistoryService analysisHistoryService;
    private final PostureGraphClient postureGraphClient;

    // Cloudinary URL을 받아 분석 요청
    @PostMapping("/user/{userId}")
    public ResponseEntity<AnalysisHistoryDTO> createAnalysisHistory(
            @PathVariable int userId,
            @RequestBody AnalysisRequestDTO requestDTO) {

        // 1. Python 서버에 분석 요청 (Cloudinary URL 사용)
        Map<String, Object> result = postureGraphClient.analyzeWithGraph(requestDTO.getImageUrl(), requestDTO.getMode());

        // 2. 결과 DTO 생성
        AnalysisHistoryDTO dto = new AnalysisHistoryDTO();
        dto.setUserId(userId);
        dto.setFrontImageUrl(requestDTO.getImageUrl());
        dto.setDiagnosis((String) result.get("diagnosis"));
        dto.setRadarChartUrl((String) result.get("radar_chart_url"));
        
        // 점수 등 추가 필드도 result에서 추출해 dto에 set (예시)
        if (result.get("spineCurvScore") != null) dto.setSpineCurvScore((Integer) result.get("spineCurvScore"));
        if (result.get("spineScolScore") != null) dto.setSpineScolScore((Integer) result.get("spineScolScore"));
        if (result.get("pelvicScore") != null) dto.setPelvicScore((Integer) result.get("pelvicScore"));
        if (result.get("neckScore") != null) dto.setNeckScore((Integer) result.get("neckScore"));
        if (result.get("shoulderScore") != null) dto.setShoulderScore((Integer) result.get("shoulderScore"));
        Object feedbackObj = result.get("feedback");
        if (feedbackObj instanceof Map) {
            dto.setFeedback((Map<String, Object>) feedbackObj);
        }
        Object measurementsObj = result.get("measurements");
        if (measurementsObj instanceof Map) {
            dto.setMeasurements((Map<String, Object>) measurementsObj);
        }

        // 3. DB 저장
        AnalysisHistoryDTO saved = analysisHistoryService.createAnalysisHistory(dto, userId);

        // 4. 저장된 DTO 반환 (id 포함)
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
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
