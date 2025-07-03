package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    // 운동 전체 조회
    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    // 운동 단건 조회
    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDTO> getExercise(@PathVariable int exerciseId) {
        ExerciseDTO exerciseDto = exerciseService.getExerciseById(exerciseId);
        return ResponseEntity.ok(exerciseDto);
    }

    // 운동 이름으로 검색
    @GetMapping("/search/name")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByName(@RequestParam String name) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByName(name);
        return ResponseEntity.ok(exercises);
    }

    // 운동 카테고리로 검색
    @GetMapping("/search/category")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByCategory(@RequestParam String category) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByCategory(category);
        return ResponseEntity.ok(exercises);
    }

    // 운동 생성
    @PostMapping
    public ResponseEntity<Integer> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
        Integer id = exerciseService.createExercise(exerciseDTO);
        return ResponseEntity.ok(id);
    }

    // 운동 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable int id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
