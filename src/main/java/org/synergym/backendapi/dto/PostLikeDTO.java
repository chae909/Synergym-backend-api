package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor  // 기본 생성자 (Jackson 직렬화용)
@AllArgsConstructor  // 모든 필드 생성자 (@Builder와 함께 사용)
@Builder  // 빌더 패턴 지원
public class PostLikeDTO {

    // Request/Response 공통 필드
    private Integer userId;  // 좋아요 누른 사용자 ID
    private String userName;  // 좋아요 누른 사용자 이름 (Response용)
    private Integer postId;  // 게시글 ID
    private String postTitle;  // 게시글 제목 (Response용)
} 