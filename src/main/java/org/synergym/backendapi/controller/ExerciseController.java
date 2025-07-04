package org.synergym.backendapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.service.ExerciseService;

import lombok.RequiredArgsConstructor;

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

    // 좋아요 수 기준 인기 운동 조회
    @GetMapping("/popular/likes")
    public ResponseEntity<List<ExerciseDTO>> getPopularExercisesByLikes(@RequestParam(defaultValue = "10") int limit) {
        List<ExerciseDTO> exercises = exerciseService.getPopularExercisesByLikes(limit);
        return ResponseEntity.ok(exercises);
    }

    // 루틴 사용 횟수 기준 인기 운동 조회
    @GetMapping("/popular/routines")
    public ResponseEntity<List<ExerciseDTO>> getPopularExercisesByRoutines(@RequestParam(defaultValue = "10") int limit) {
        List<ExerciseDTO> exercises = exerciseService.getPopularExercisesByRoutines(limit);
        return ResponseEntity.ok(exercises);
    }
}
