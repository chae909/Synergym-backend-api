package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.PostLikeDTO;
import org.synergym.backendapi.entity.PostLike;

import java.util.List;

public interface PostLikeService {

    // 좋아요 생성 (좋아요 누르기)
    void createPostLike(PostLikeDTO postLikeDTO);

    // 좋아요 삭제 (좋아요 취소)
    void deletePostLike(Integer userId, Integer postId);

    // 사용자별 좋아요 조회
    List<PostLikeDTO> getPostLikesByUserId(Integer userId);

    // 게시글별 좋아요 조회
    List<PostLikeDTO> getPostLikesByPostId(Integer postId);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    // 게시글의 좋아요 수 조회
    long getLikeCountByPostId(Integer postId);

    // 사용자가 누른 좋아요 수 조회
    long getLikeCountByUserId(Integer userId);

    // DTO -> Entity 변환
    default PostLike DTOtoEntity(PostLikeDTO dto) {
        return PostLike.builder()
                .build();
    }

    // Entity -> DTO 변환
    default PostLikeDTO entityToDTO(PostLike postLike) {
        return PostLikeDTO.builder()
                .userId(postLike.getUser().getId())
                .userName(postLike.getUser().getName())
                .postId(postLike.getPost().getId())
                .postTitle(postLike.getPost().getTitle())
                .build();
    }
} 