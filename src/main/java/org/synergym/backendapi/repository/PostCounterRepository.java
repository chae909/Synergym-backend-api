package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.PostCounter;

import java.util.Optional;

public interface PostCounterRepository extends JpaRepository<PostCounter, Integer> {
    
    Optional<PostCounter> findByPostId(Integer postId);
    
    @Modifying
    @Query("UPDATE PostCounter pc SET pc.likeCount = pc.likeCount + 1 WHERE pc.postId = :postId")
    void incrementLikeCount(@Param("postId") Integer postId);
    
    @Modifying
    @Query("UPDATE PostCounter pc SET pc.likeCount = CASE WHEN pc.likeCount > 0 THEN pc.likeCount - 1 ELSE 0 END WHERE pc.postId = :postId")
    void decrementLikeCount(@Param("postId") Integer postId);
} 