package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.PostDTO;
import org.synergym.backendapi.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 페이징 게시글 조회 (최신순)
    @GetMapping("/paging")
    public ResponseEntity<Page<PostDTO>> getPostsWithPaging(Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsWithPaging(pageable));
    }

    // 페이징 게시글 조회 (인기순)
    @GetMapping("/paging/popular")
    public ResponseEntity<Page<PostDTO>> getPostsWithPagingByPopularity(Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsWithPagingByPopularity(pageable));
    }

    // 단일 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<Integer> createPost(@RequestBody PostDTO postDTO) {
        Integer id = postService.createPost(postDTO);
        return ResponseEntity.ok(id);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable int id, @RequestBody PostDTO postDTO) {
        postService.updatePost(id, postDTO);
        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글 검색 (제목/내용)
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    // 사용자별 게시글 조회 (페이징)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostsByUserIdWithPaging(@PathVariable Integer userId, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUserIdWithPaging(userId, pageable));
    }

    // 카테고리별 게시글 조회 (페이징, 최신순)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostDTO>> getPostsByCategoryIdWithPaging(@PathVariable Integer categoryId, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategoryIdWithPaging(categoryId, pageable));
    }

    // 카테고리별 게시글 조회 (페이징, 인기순)
    @GetMapping("/category/{categoryId}/popular")
    public ResponseEntity<Page<PostDTO>> getPostsByCategoryIdWithPagingByPopularity(@PathVariable Integer categoryId, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategoryIdWithPagingByPopularity(categoryId, pageable));
    }
}