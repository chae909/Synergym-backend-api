package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.service.ExerciseLikeService;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-likes")
@RequiredArgsConstructor
public class ExerciseLikeController {

    private final ExerciseLikeService exerciseLikeService;

    // 운동 좋아요 추가
    @PostMapping
    public ResponseEntity<Void> addLike(@RequestBody ExerciseLikeDTO exerciseLikeDTO) {
        exerciseLikeService.add(exerciseLikeDTO);
        return ResponseEntity.ok().build();
    }

    // 운동 좋아요 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteLike(@RequestParam Integer userId, @RequestParam Integer exerciseId) {
        exerciseLikeService.delete(userId, exerciseId);
        return ResponseEntity.noContent().build();
    }

    // 특정 사용자의 모든 운동 좋아요 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseLikeDTO>> getLikesByUser(@PathVariable Integer userId) {
        List<ExerciseLikeDTO> likes = exerciseLikeService.getByUser(userId);
        return ResponseEntity.ok(likes);
    }

    // 특정 운동의 모든 좋아요 조회
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<ExerciseLikeDTO>> getLikesByExercise(@PathVariable Integer exerciseId) {
        List<ExerciseLikeDTO> likes = exerciseLikeService.getByExercise(exerciseId);
        return ResponseEntity.ok(likes);
    }

    // 특정 사용자가 특정 운동을 좋아요 했는지 확인
    @GetMapping("/is-liked")
    public ResponseEntity<Boolean> isLiked(@RequestParam Integer userId, @RequestParam Integer exerciseId) {
        boolean liked = exerciseLikeService.isLiked(userId, exerciseId);
        return ResponseEntity.ok(liked);
    }
} 