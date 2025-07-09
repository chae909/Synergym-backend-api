package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 게시글 좋아요 관계 DTO
// 개별 사용자가 특정 게시글에 좋아요를 눌렀는지 관리
public class PostLikeDTO {

    // Request/Response 공통 필드
    private Integer userId;  // 좋아요 누른 사용자 ID
    private String userName;  // 좋아요 누른 사용자 이름 (Response용)
    private Integer postId;  // 게시글 ID
    private String postTitle;  // 게시글 제목 (Response용)
} 