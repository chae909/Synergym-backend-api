package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 게시글 집계 데이터 DTO
// 프론트엔드에서 게시글 목록/상세에서 "좋아요 수, 댓글 수, 조회 수" 표시용
public class PostCounterDTO {

    // Request/Response 공통 필드
    private Integer postId;  // 게시글 ID
    private Integer likeCount;  // 좋아요 수
    private Integer commentCount;  // 댓글 수
    private Integer viewCount;  // 조회 수
} 