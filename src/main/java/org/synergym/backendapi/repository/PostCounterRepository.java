package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.PostCounter;

import java.util.Optional;


//게시글 집계 데이터 Repository
//게시글의 좋아요 수, 댓글 수, 조회 수 집계 데이터 관리
public interface PostCounterRepository extends JpaRepository<PostCounter, Integer> {
    
    // 특정 게시글의 카운터 조회
    Optional<PostCounter> findByPostId(Integer postId);
    
    // 게시글 좋아요 수 증가 (집계 업데이트)
    @Modifying
    @Query("UPDATE PostCounter pc SET pc.likeCount = pc.likeCount + 1 WHERE pc.postId = :postId")
    void incrementLikeCount(@Param("postId") Integer postId);
    
    // 게시글 좋아요 수 감소 (집계 업데이트)
    @Modifying
    @Query("UPDATE PostCounter pc SET pc.likeCount = CASE WHEN pc.likeCount > 0 THEN pc.likeCount - 1 ELSE 0 END WHERE pc.postId = :postId")
    void decrementLikeCount(@Param("postId") Integer postId);
} 