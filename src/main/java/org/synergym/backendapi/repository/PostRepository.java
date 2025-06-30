package org.synergym.backendapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    
    // 제목 또는 내용으로 검색 (부분 일치)
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
    
    // 사용자별 게시글 조회 (페이징)
    Page<Post> findByUserId(Integer userId, Pageable pageable);
    
    // 카테고리별 게시글 조회 (페이징, 최신순)
    Page<Post> findByCategoryIdOrderByCreatedAtDesc(Integer categoryId, Pageable pageable);
    
    // 카테고리별 게시글 조회 (페이징, 인기순)
    Page<Post> findByCategoryIdOrderByLikeCountDesc(Integer categoryId, Pageable pageable);
    
    // 전체 게시글 조회 (페이징, 최신순)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 전체 게시글 조회 (페이징, 인기순)
    Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
}
