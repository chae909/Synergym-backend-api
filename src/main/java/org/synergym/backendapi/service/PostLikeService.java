package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.PostLikeDTO;
import org.synergym.backendapi.entity.PostLike;

import java.util.List;


//게시글 좋아요 관계 서비스
//개별 사용자의 좋아요 생성/삭제 및 조회 관리
public interface PostLikeService {

    // 좋아요 생성 (사용자가 게시글에 좋아요 누르기)
    // 생성 후 PostCounterService.incrementLikeCount() 호출하여 집계 업데이트
    void createPostLike(PostLikeDTO postLikeDTO);

    // 좋아요 삭제 (사용자가 게시글 좋아요 취소)
    // 삭제 후 PostCounterService.decrementLikeCount() 호출하여 집계 업데이트
    void deletePostLike(Integer userId, Integer postId);

    // 사용자별 좋아요 조회
    List<PostLikeDTO> getPostLikesByUserId(Integer userId);

    // 게시글별 좋아요 조회
    List<PostLikeDTO> getPostLikesByPostId(Integer postId);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    // 프론트엔드에서 "좋아요 버튼 활성화/비활성화" 상태 확인용
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