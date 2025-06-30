package org.synergym.backendapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // 게시글별 댓글 조회 (페이징, 최신순)
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Integer postId, Pageable pageable);
    
    // 게시글별 댓글 조회 (페이징, 오래된순)
    Page<Comment> findByPostIdOrderByCreatedAtAsc(Integer postId, Pageable pageable);
    
    // 사용자별 댓글 조회 (페이징)
    Page<Comment> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // 게시글별 댓글 수 조회
    long countByPostId(Integer postId);
    
    // 사용자별 댓글 수 조회
    long countByUserId(Integer userId);
    
    // 내용으로 댓글 검색 (부분 일치)
    List<Comment> findByContentContaining(String content);
}
