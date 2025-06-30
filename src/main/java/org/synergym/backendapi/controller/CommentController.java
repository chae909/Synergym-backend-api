package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.CommentDTO;
import org.synergym.backendapi.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<Integer> createComment(@RequestBody CommentDTO commentDTO) {
        Integer id = commentService.createComment(commentDTO);
        return ResponseEntity.ok(id);
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComment(@PathVariable int id, @RequestBody CommentDTO commentDTO) {
        commentService.updateComment(id, commentDTO);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // ID로 댓글 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // 게시글별 댓글 조회 (페이징, 최신순)
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostIdWithPaging(@PathVariable Integer postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPostIdWithPaging(postId, pageable));
    }

    // 게시글별 댓글 조회 (페이징, 오래된순)
    @GetMapping("/post/{postId}/asc")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPostIdWithPagingAsc(@PathVariable Integer postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPostIdWithPagingAsc(postId, pageable));
    }

    // 사용자별 댓글 조회 (페이징)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByUserIdWithPaging(@PathVariable Integer userId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUserIdWithPaging(userId, pageable));
    }

    // 게시글별 댓글 수 조회
    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Long> getCommentCountByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentCountByPostId(postId));
    }

    // 사용자별 댓글 수 조회
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getCommentCountByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(commentService.getCommentCountByUserId(userId));
    }

    // 내용으로 댓글 검색
    @GetMapping("/search")
    public ResponseEntity<List<CommentDTO>> searchComments(@RequestParam String keyword) {
        return ResponseEntity.ok(commentService.searchComments(keyword));
    }
}