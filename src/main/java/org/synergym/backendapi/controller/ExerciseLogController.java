package org.synergym.backendapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.dto.WeeklyMonthlyStats;
import org.synergym.backendapi.service.ExerciseLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ExerciseLogController {

    private final ExerciseLogService exerciseLogService;

    // 운동기록 전체 조회
    @GetMapping
    public ResponseEntity<List<ExerciseLogDTO>> getAllExerciseLogs() {
        List<ExerciseLogDTO> logs = exerciseLogService.getAllExerciseLogs();
        return ResponseEntity.ok(logs);
    }

    // 운동기록 업데이트
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateExerciseLog(@PathVariable int id, @RequestBody ExerciseLogDTO exerciseLogDTO) {
        exerciseLogService.updateExerciseLog(id, exerciseLogDTO);
        return ResponseEntity.ok().build();
    }

    // 운동기록 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseLogDTO> getExerciseLog(@PathVariable int id) {
        ExerciseLogDTO log = exerciseLogService.getExerciseLogById(id);
        return ResponseEntity.ok(log);
    }

    // 사용자별 운동기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseLogDTO>> getExerciseLogsByUser(@PathVariable Integer userId) {
        List<ExerciseLogDTO> logs = exerciseLogService.getExerciseLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }

    // 사용자별 + 날짜별 운동기록 조회
    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<ExerciseLogDTO>> getExerciseLogsByUserAndDate(
            @PathVariable Integer userId,
            @RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<ExerciseLogDTO> logs = exerciseLogService.getExerciseLogsByUserAndDate(userId, date);
        return ResponseEntity.ok(logs);
    }

    // 운동기록 생성
    @PostMapping
    public ResponseEntity<Integer> createExerciseLog(@RequestBody ExerciseLogDTO exerciseLogDTO) {
        Integer id = exerciseLogService.createExerciseLog(exerciseLogDTO);
        return ResponseEntity.ok(id);
    }

    // 운동기록 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseLog(@PathVariable int id) {
        exerciseLogService.deleteExerciseLog(id);
        return ResponseEntity.noContent().build();
    }

    // 사용자별 주간 통계 조회
    @GetMapping("/user/{userId}/week")
    public ResponseEntity<WeeklyMonthlyStats> getWeeklyStats(@PathVariable Integer userId) {
        WeeklyMonthlyStats stats = exerciseLogService.getWeeklyStats(userId);
        return ResponseEntity.ok(stats);
    }

    // 사용자별 월간 통계 조회
    @GetMapping("/user/{userId}/month")
    public ResponseEntity<WeeklyMonthlyStats> getMonthlyStats(@PathVariable Integer userId) {
        WeeklyMonthlyStats stats = exerciseLogService.getMonthlyStats(userId);
        return ResponseEntity.ok(stats);
    }
}