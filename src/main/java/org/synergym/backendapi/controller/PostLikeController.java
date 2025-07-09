package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.PostLikeDTO;
import org.synergym.backendapi.service.PostLikeService;

import java.util.List;

@RestController
@RequestMapping("/api/post-likes")
@RequiredArgsConstructor


//게시글 좋아요 관계 컨트롤러
//개별 사용자의 좋아요 생성/삭제 및 조회 관리
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 생성
    @PostMapping
    public ResponseEntity<Void> createPostLike(@RequestBody PostLikeDTO postLikeDTO) {
        postLikeService.createPostLike(postLikeDTO);
        return ResponseEntity.ok().build();
    }

    // 좋아요 삭제
    @DeleteMapping
    public ResponseEntity<Void> deletePostLike(@RequestParam Integer userId, @RequestParam Integer postId) {
        postLikeService.deletePostLike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    // 사용자별 좋아요 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostLikeDTO>> getPostLikesByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(postLikeService.getPostLikesByUserId(userId));
    }

    // 게시글별 좋아요 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostLikeDTO>> getPostLikesByPost(@PathVariable Integer postId) {
        return ResponseEntity.ok(postLikeService.getPostLikesByPostId(postId));
    }

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByUserIdAndPostId(@RequestParam Integer userId, @RequestParam Integer postId) {
        return ResponseEntity.ok(postLikeService.existsByUserIdAndPostId(userId, postId));
    }

    // 게시글별 좋아요 수
    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Long> getLikeCountByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(postLikeService.getLikeCountByPostId(postId));
    }

    // 사용자별 좋아요 수
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getLikeCountByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(postLikeService.getLikeCountByUserId(userId));
    }
}