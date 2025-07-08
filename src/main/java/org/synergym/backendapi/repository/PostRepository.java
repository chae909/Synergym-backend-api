package org.synergym.backendapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    
    // 제목 또는 내용으로 검색 (부분 일치)
    List<Post> findByTitleContainingOrContentContaining(String title, String content);
    
    // 사용자별 게시글 조회 (페이징)
    Page<Post> findByUserId(Integer userId, Pageable pageable);
    
    // 카테고리별 게시글 조회 (페이징, 최신순)
    Page<Post> findByCategoryIdOrderByCreatedAtDesc(Integer categoryId, Pageable pageable);
    
    // 전체 게시글 조회 (페이징, 최신순)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 카테고리별 게시글 조회 (페이징, 인기순) - PostCounter와 조인하여 안정적으로 처리
    @Query("SELECT p FROM Post p LEFT JOIN p.postCounter pc WHERE p.category.id = :categoryId ORDER BY COALESCE(pc.likeCount, 0) DESC")
    Page<Post> findByCategoryIdOrderByLikeCountDesc(@Param("categoryId") Integer categoryId, Pageable pageable);
    
    // 전체 게시글 조회 (페이징, 인기순) - PostCounter와 조인하여 안정적으로 처리
    @Query("SELECT p FROM Post p LEFT JOIN p.postCounter pc ORDER BY COALESCE(pc.likeCount, 0) DESC")
    Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
    
    // 조회수 순으로 인기 게시글 조회 (상위 10개)
    @Query("SELECT p.title, COALESCE(pc.viewCount, 0), c.name, p.id " +
           "FROM Post p LEFT JOIN p.postCounter pc LEFT JOIN p.category c " +
           "ORDER BY COALESCE(pc.viewCount, 0) DESC")
    List<Object[]> findPopularPostsByViews();
    
    // 댓글수 순으로 인기 게시글 조회 (상위 10개)
    @Query("SELECT p.title, COALESCE(pc.commentCount, 0), c.name, p.id " +
           "FROM Post p LEFT JOIN p.postCounter pc LEFT JOIN p.category c " +
           "ORDER BY COALESCE(pc.commentCount, 0) DESC")
    List<Object[]> findPopularPostsByComments();
    
    // 좋아요 순으로 인기 게시글 조회 (상위 10개)
    @Query("SELECT p.title, COALESCE(pc.likeCount, 0), c.name, p.id " +
           "FROM Post p LEFT JOIN p.postCounter pc LEFT JOIN p.category c " +
           "ORDER BY COALESCE(pc.likeCount, 0) DESC")
    List<Object[]> findPopularPostsByLikes();

}
