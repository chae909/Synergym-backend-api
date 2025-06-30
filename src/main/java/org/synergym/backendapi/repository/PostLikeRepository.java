package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.PostLike;
import org.synergym.backendapi.entity.PostLikeId;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    
    // 사용자별 좋아요 조회
    List<PostLike> findByUserId(Integer userId);
    
    // 게시글별 좋아요 조회
    List<PostLike> findByPostId(Integer postId);
    
    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<PostLike> findByUserIdAndPostId(Integer userId, Integer postId);
    
    // 게시글의 좋아요 수 조회
    long countByPostId(Integer postId);
    
    // 사용자가 누른 좋아요 수 조회
    long countByUserId(Integer userId);
}
