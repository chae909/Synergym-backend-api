package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.PostCounterDTO;
import org.synergym.backendapi.service.PostCounterService;

@Slf4j
@RestController
@RequestMapping("/api/post-counters")
@RequiredArgsConstructor

/*
 * 게시글 집계 데이터 컨트롤러
 * 게시글의 좋아요 수, 댓글 수, 조회 수를 빠르게 조회하기 위한 API
 */
public class PostCounterController {

    private final PostCounterService postCounterService;

    // 게시글의 모든 집계 데이터 조회 (좋아요 수, 댓글 수, 조회 수)
    @GetMapping("/{postId}")
    public ResponseEntity<PostCounterDTO> getCounter(@PathVariable Integer postId) {
        try {
            log.debug("Getting counter for postId: {}", postId);
            PostCounterDTO counter = postCounterService.getCounter(postId);
            return ResponseEntity.ok(counter);
        } catch (Exception e) {
            log.error("Error getting counter for postId: {}", postId, e);
            // 에러 발생 시 기본값 반환
            PostCounterDTO defaultCounter = PostCounterDTO.builder()
                    .postId(postId)
                    .likeCount(0)
                    .commentCount(0)
                    .viewCount(0)
                    .build();
            return ResponseEntity.ok(defaultCounter);
        }
    }

    // 게시글의 좋아요 수만 조회 (집계된 숫자)
    @GetMapping("/{postId}/like-count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Integer postId) {
        try {
            log.debug("Getting like count for postId: {}", postId);
            Integer likeCount = postCounterService.getLikeCount(postId);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            log.error("Error getting like count for postId: {}", postId, e);
            return ResponseEntity.ok(0);
        }
    }

    // 게시글 댓글 수만 조회
    @GetMapping("/{postId}/comment-count")
    public ResponseEntity<Integer> getCommentCount(@PathVariable Integer postId) {
        try {
            log.debug("Getting comment count for postId: {}", postId);
            Integer commentCount = postCounterService.getCommentCount(postId);
            return ResponseEntity.ok(commentCount);
        } catch (Exception e) {
            log.error("Error getting comment count for postId: {}", postId, e);
            return ResponseEntity.ok(0);
        }
    }

    // 게시글 조회 수만 조회
    @GetMapping("/{postId}/view-count")
    public ResponseEntity<Integer> getViewCount(@PathVariable Integer postId) {
        try {
            log.debug("Getting view count for postId: {}", postId);
            Integer viewCount = postCounterService.getViewCount(postId);
            return ResponseEntity.ok(viewCount);
        } catch (Exception e) {
            log.error("Error getting view count for postId: {}", postId, e);
            return ResponseEntity.ok(0);
        }
    }

    // 게시글 조회 수 증가
    @PostMapping("/{postId}/view-count")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer postId) {
        try {
            log.debug("Incrementing view count for postId: {}", postId);
            postCounterService.incrementViewCount(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error incrementing view count for postId: {}", postId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 댓글 수 동기화 (마이그레이션용)
    @PostMapping("/sync-comment-counts")
    public ResponseEntity<String> syncCommentCounts() {
        try {
            log.info("Starting comment count synchronization");
            postCounterService.syncCommentCounts();
            log.info("Comment count synchronization completed");
            return ResponseEntity.ok("댓글 수 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("Error during comment count synchronization", e);
            return ResponseEntity.internalServerError().body("댓글 수 동기화 중 오류가 발생했습니다.");
        }
    }
} 