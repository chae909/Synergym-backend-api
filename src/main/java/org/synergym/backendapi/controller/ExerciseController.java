// package org.synergym.backendapi.controller;

// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.synergym.backendapi.dto.ExerciseDTO;
// import org.synergym.backendapi.service.ExerciseService;

// import java.util.List;

// @RestController
// @RequestMapping("/api/exercises")
// @RequiredArgsConstructor
// public class ExerciseController {

//     private final ExerciseService exerciseService;

//     // 운동 생성
//     @PostMapping
//     public ResponseEntity<Integer> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
//         try {
//             Integer exerciseId = exerciseService.saveExercise(exerciseDTO);
//             return ResponseEntity.status(HttpStatus.CREATED).body(exerciseId);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//         }
//     }

//     // 모든 운동 조회
//     @GetMapping
//     public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
//         try {
//             List<ExerciseDTO> exercises = exerciseService.findAllExercises();
//             return ResponseEntity.ok(exercises);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     // ID로 운동 조회
//     @GetMapping("/{id}")
//     public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Integer id) {
//         try {
//             ExerciseDTO exercise = exerciseService.findExerciseById(id);
//             return ResponseEntity.ok(exercise);
//         } catch (RuntimeException e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     // ID로 운동 삭제
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
//         try {
//             exerciseService.deleteExercise(id);
//             return ResponseEntity.noContent().build();
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }
// }