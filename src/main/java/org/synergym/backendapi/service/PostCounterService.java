package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.PostCounterDTO;
import org.synergym.backendapi.entity.PostCounter;

public interface PostCounterService {

    /**
     * 게시글 좋아요 수 증가
     */
    void incrementLikeCount(Integer postId);

    /**
     * 게시글 좋아요 수 감소
     */
    void decrementLikeCount(Integer postId);

    /**
     * 게시글 댓글 수 증가
     */
    void incrementCommentCount(Integer postId);

    /**
     * 게시글 댓글 수 감소
     */
    void decrementCommentCount(Integer postId);

    /**
     * 게시글 조회 수 증가
     */
    void incrementViewCount(Integer postId);

    /**
     * 게시글의 모든 카운터 정보 조회
     */
    PostCounterDTO getCounter(Integer postId);

    /**
     * 게시글의 좋아요 수만 조회
     */
    Integer getLikeCount(Integer postId);

    /**
     * 게시글의 댓글 수만 조회
     */
    Integer getCommentCount(Integer postId);

    /**
     * 게시글의 조회 수만 조회
     */
    Integer getViewCount(Integer postId);

    /**
     * 기존 댓글들의 commentCount를 PostCounter에 반영 (마이그레이션용)
     */
    void syncCommentCounts();

    // DTO -> Entity 변환
    default PostCounter dtoToEntity(PostCounterDTO dto) {
        if (dto == null) {
            return null;
        }
        PostCounter counter = new PostCounter(null); // post는 서비스에서 설정
        counter.setLikeCount(dto.getLikeCount());
        counter.setCommentCount(dto.getCommentCount());
        counter.setViewCount(dto.getViewCount());
        return counter;
    }

    // Entity -> DTO 변환
    default PostCounterDTO entityToDTO(PostCounter postCounter) {
        if (postCounter == null) {
            return null;
        }
        return PostCounterDTO.builder()
                .postId(postCounter.getPostId())
                .likeCount(postCounter.getLikeCount())
                .commentCount(postCounter.getCommentCount())
                .viewCount(postCounter.getViewCount())
                .build();
    }
} 