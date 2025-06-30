package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.service.ExerciseLogService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exercise-logs")
@RequiredArgsConstructor
public class ExerciseLogController {

    private final ExerciseLogService exerciseLogService;

    // 운동기록 전체 조회
    @GetMapping
    public ResponseEntity<List<ExerciseLogDTO>> getAllExerciseLogs() {
        List<ExerciseLogDTO> logs = exerciseLogService.getAllExerciseLogs();
        return ResponseEntity.ok(logs);
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
} 