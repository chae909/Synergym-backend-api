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
        
        // Handle diagnosis field - convert Map to JSON string if needed
        Object diagnosisObj = result.get("diagnosis");
        if (diagnosisObj instanceof Map) {
            try {
                String diagnosisJson = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(diagnosisObj);
                dto.setDiagnosis(diagnosisJson);
            } catch (Exception e) {
                dto.setDiagnosis("진단 정보 변환 실패");
            }
        } else if (diagnosisObj instanceof String) {
            dto.setDiagnosis((String) diagnosisObj);
        } else if (diagnosisObj == null) {
            dto.setDiagnosis("진단 정보가 없습니다.");
        } else {
            dto.setDiagnosis(diagnosisObj.toString());
        }
        
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
        // --- measurements 항상 DTO에 넣기 ---
        Object poseDataObj = result.get("pose_data");
        if (poseDataObj instanceof Map) {
            Map<String, Object> poseMap = (Map<String, Object>) poseDataObj;
            Object measurementsObj = poseMap.get("measurements");
            System.out.println("[단일] measurements: " + measurementsObj);
            System.out.println("[단일] measurements type: " + (measurementsObj != null ? measurementsObj.getClass() : "null"));
            if (measurementsObj instanceof Map) {
                dto.setMeasurements((Map<String, Object>) measurementsObj);
            }
        }
        // 3. DB 저장
        AnalysisHistoryDTO saved = analysisHistoryService.createAnalysisHistory(dto, userId);

        // 4. 저장된 DTO 반환 (id 포함)
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 정면/측면 이미지를 한 번에 받아 merge 분석 요청
    @PostMapping("/merge/user/{userId}")
    public ResponseEntity<AnalysisHistoryDTO> createMergedAnalysisHistory(
            @PathVariable int userId,
            @RequestBody Map<String, String> requestBody) {

        String frontImageUrl = requestBody.get("front_image_url");
        String sideImageUrl = requestBody.get("side_image_url");

        // 1. Python 서버에 merge 분석 요청
        Map<String, Object> result = postureGraphClient.analyzeWithGraphMerge(frontImageUrl, sideImageUrl);

        // 2. 결과 DTO 생성 및 저장 (기존 createAnalysisHistory와 유사하게)
        AnalysisHistoryDTO dto = new AnalysisHistoryDTO();
        dto.setUserId(userId);
        dto.setFrontImageUrl(frontImageUrl);
        dto.setSideImageUrl(sideImageUrl);
        // Handle diagnosis field - convert Map to JSON string if needed
        Object diagnosisObj = result.get("diagnosis");
        if (diagnosisObj instanceof Map) {
            try {
                String diagnosisJson = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(diagnosisObj);
                dto.setDiagnosis(diagnosisJson);
            } catch (Exception e) {
                dto.setDiagnosis("진단 정보 변환 실패");
            }
        } else if (diagnosisObj instanceof String) {
            dto.setDiagnosis((String) diagnosisObj);
        } else if (diagnosisObj == null) {
            dto.setDiagnosis("진단 정보가 없습니다.");
        } else {
            dto.setDiagnosis(diagnosisObj.toString());
        }
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
        // --- merge measurements 합치기 ---
        Object frontPoseObj = result.get("front_pose_data");
        Object sidePoseObj = result.get("side_pose_data");
        Map<String, Object> mergedMeasurements = new java.util.HashMap<>();
        if (frontPoseObj instanceof Map) {
            Map<String, Object> frontMap = (Map<String, Object>) frontPoseObj;
            Object frontMeasurementsObj = frontMap.get("measurements");
            System.out.println("[merge] front measurements: " + frontMeasurementsObj);
            System.out.println("[merge] front measurements type: " + (frontMeasurementsObj != null ? frontMeasurementsObj.getClass() : "null"));
            Map<String, Object> frontMeasurementsMap = null;
            if (frontMeasurementsObj instanceof Map) {
                frontMeasurementsMap = (Map<String, Object>) frontMeasurementsObj;
            } else if (frontMeasurementsObj instanceof String) {
                try {
                    frontMeasurementsMap = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue((String) frontMeasurementsObj, Map.class);
                } catch (Exception e) {
                    // 변환 실패 시 무시
                }
            }
            if (frontMeasurementsMap != null) {
                for (Map.Entry<String, Object> entry : frontMeasurementsMap.entrySet()) {
                    mergedMeasurements.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (sidePoseObj instanceof Map) {
            Map<String, Object> sideMap = (Map<String, Object>) sidePoseObj;
            Object sideMeasurementsObj = sideMap.get("measurements");
            System.out.println("[merge] side measurements: " + sideMeasurementsObj);
            System.out.println("[merge] side measurements type: " + (sideMeasurementsObj != null ? sideMeasurementsObj.getClass() : "null"));
            Map<String, Object> sideMeasurementsMap = null;
            if (sideMeasurementsObj instanceof Map) {
                sideMeasurementsMap = (Map<String, Object>) sideMeasurementsObj;
            } else if (sideMeasurementsObj instanceof String) {
                try {
                    sideMeasurementsMap = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue((String) sideMeasurementsObj, Map.class);
                } catch (Exception e) {
                    // 변환 실패 시 무시
                }
            }
            if (sideMeasurementsMap != null) {
                for (Map.Entry<String, Object> entry : sideMeasurementsMap.entrySet()) {
                    if (entry.getValue() != null) {
                        mergedMeasurements.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        if (!mergedMeasurements.isEmpty()) {
            dto.setMeasurements(mergedMeasurements);
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
