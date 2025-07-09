package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.PostCounterDTO;
import org.synergym.backendapi.entity.PostCounter;


//게시글 집계 데이터 서비스
//게시글의 좋아요 수, 댓글 수, 조회 수 집계 데이터 관리
public interface PostCounterService {

    // 게시글 좋아요 수 증가
    // PostLikeService에서 좋아요 생성 시 호출됨
    void incrementLikeCount(Integer postId);

    // 게시글 좋아요 수 감소
    // PostLikeService에서 좋아요 삭제 시 호출됨
    void decrementLikeCount(Integer postId);

    // 게시글 댓글 수 증가
    void incrementCommentCount(Integer postId);

    // 게시글 댓글 수 감소
    void decrementCommentCount(Integer postId);

    // 게시글 조회 수 증가
    void incrementViewCount(Integer postId);

    // 게시글의 모든 집계 데이터 조회 (좋아요 수, 댓글 수, 조회 수)
    // 프론트엔드에서 게시글 목록/상세 표시용
    PostCounterDTO getCounter(Integer postId);

    // 게시글의 좋아요 수만 조회
    Integer getLikeCount(Integer postId);

    // 게시글의 댓글 수만 조회
    Integer getCommentCount(Integer postId);

    // 게시글의 조회 수만 조회
    Integer getViewCount(Integer postId);

    // 기존 댓글들의 commentCount를 PostCounter에 반영 (마이그레이션용)
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