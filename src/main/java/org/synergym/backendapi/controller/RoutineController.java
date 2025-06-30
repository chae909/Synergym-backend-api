package org.synergym.backendapi.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.RoutineDTO;
import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.dto.UpdateOrderRequest;
import org.synergym.backendapi.service.RoutineExerciseService;
import org.synergym.backendapi.service.RoutineService;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;
    private final RoutineExerciseService routineExerciseService;

    // 특정 사용자의 투틴 생성
    @PostMapping("/user/{userId}")
    public ResponseEntity<RoutineDTO> createRoutine(
            @RequestBody RoutineDTO routineDTO,
            @PathVariable int userId) {
        RoutineDTO createdRoutine = routineService.createRoutine(routineDTO, userId);
        return new ResponseEntity<>(createdRoutine, HttpStatus.CREATED);
    }

    // 특정 사용자의 모든 루틴 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoutineDTO>> getRoutinesByUserId(@PathVariable int userId) {
        List<RoutineDTO> routines = routineService.getRoutinesByUserId(userId);
        return ResponseEntity.ok(routines);
    }

    // 이름으로 루틴 검색
    @GetMapping("/search")
    public ResponseEntity<List<RoutineDTO>> getRoutinesByName(@RequestParam String name) {
        List<RoutineDTO> routines = routineService.getRoutinesByName(name);
        return ResponseEntity.ok(routines);
    }

    // Id로 특정 루틴 상세 정보 조회
    @GetMapping("/{routineId}")
    public ResponseEntity<RoutineDTO> getRoutineDetails(@PathVariable int routineId) {
        RoutineDTO routine = routineService.getRoutineDetails(routineId);
        return ResponseEntity.ok(routine);
    }

    // 루틴 정보 수정
    @PutMapping("/{routineId}")
    public ResponseEntity<RoutineDTO> updateRoutine(@PathVariable int routineId, @RequestBody RoutineDTO routineDTO) {
        RoutineDTO updatedRoutine = routineService.updateRoutine(routineId, routineDTO);
        return ResponseEntity.ok(updatedRoutine);
    }

    // 루틴 삭제
    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable int routineId) {
        routineService.deleteRoutine(routineId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 루틴에 운동 추가
    @PostMapping("/{routineId}/exercises")
    public ResponseEntity<RoutineExerciseDTO> addExerciseToRoutine(
            @PathVariable int routineId,
            @RequestBody RoutineExerciseDTO requestDTO) {
        requestDTO.setRoutineId(routineId);
        RoutineExerciseDTO addedExercise = routineExerciseService.addExerciseToRoutine(requestDTO);
        return new ResponseEntity<>(addedExercise, HttpStatus.CREATED);
    }

    // 특정 루틴에 포함된 모든 운동 조회
    @GetMapping("/{routineId}/exercises")
    public ResponseEntity<List<RoutineExerciseDTO>> getAllExercisesInRoutine(@PathVariable int routineId) {
        List<RoutineExerciseDTO> exercises = routineExerciseService.getAllExercisesByRoutineId(routineId);
        return ResponseEntity.ok(exercises);
    }

    // 루틴 내 운동 순서 변경
    @PatchMapping("/{routineId}/exercises/{exerciseId}")
    public ResponseEntity<RoutineExerciseDTO> updateExerciseOrder(
            @PathVariable int routineId,
            @PathVariable int exerciseId,
            @RequestBody UpdateOrderRequest request) {
        RoutineExerciseDTO updated = routineExerciseService.updateExerciseOrder(routineId, exerciseId, request.getOrder());
        return ResponseEntity.ok(updated);
    }

    // 루틴에서 운동 제거
    @DeleteMapping("/{routineId}/exercises/{exerciseId}")
    public ResponseEntity<Void> removeExerciseFromRoutine(
            @PathVariable int routineId,
            @PathVariable int exerciseId) {
        routineExerciseService.removeExerciseFromRoutine(routineId, exerciseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
