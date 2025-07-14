package org.synergym.backendapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.synergym.backendapi.dto.EmotionLogDTO;
import org.synergym.backendapi.dto.EmotionStatsDTO;
import org.synergym.backendapi.service.EmotionLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emotion-logs")
@RequiredArgsConstructor
public class EmotionLogController {
    
    private final EmotionLogService emotionLogService;

    // 사용자별 전체 감성 기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmotionLogDTO>> getEmotionLogsByUser(@PathVariable Integer userId) {
        List<EmotionLogDTO> logs = emotionLogService.getEmotionLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }

    // 감성 기록 생성 또는 수정
    @PostMapping
    public ResponseEntity<EmotionLogDTO> saveOrUpdateEmotionLog(@RequestBody EmotionLogDTO emotionLogDTO) {
        EmotionLogDTO resultLog = emotionLogService.saveOrUpdateEmotionLog(emotionLogDTO);
        return ResponseEntity.ok(resultLog);
    }

    // 감성 기록 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmotionLog(@PathVariable Integer id) {
        emotionLogService.deleteEmotionLog(id);
        return ResponseEntity.noContent().build();
    }

    // 사용자별 감성 기록 통계 조회
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<EmotionStatsDTO> getEmotionStats(@PathVariable Integer userId) {
        EmotionStatsDTO stats = emotionLogService.getEmotionStats(userId);
        return ResponseEntity.ok(stats);
    }
}