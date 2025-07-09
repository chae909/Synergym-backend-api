package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data  // getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor  // 기본 생성자 (Jackson 직렬화용)
@AllArgsConstructor  // 모든 필드 생성자 (@Builder와 함께 사용)
@Builder  // 빌더 패턴 지원

// 게시글 관련 DTO
public class PostDTO {

    // Response용 필드 (조회 시에만 사용)
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Character useYn;

    // Request/Response 공통 필드
    private Integer userId;  // 게시글 작성자 ID
    private String userName;  // 게시글 작성자 이름 (Response용)
    private Integer categoryId;  // 카테고리 ID
    private String categoryName;  // 카테고리 이름 (Response용)
    private String title;  // 제목
    private String content;  // 내용
    private String imageUrl;  // 이미지 URL
    private Integer likeCount;  // 좋아요 수
    private Integer commentCount;  // 댓글 수
    private Integer viewCount;  // 조회 수
} 