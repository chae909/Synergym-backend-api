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

// 커뮤니티 카테고리 관련 DTO
public class CategoryDTO {

    // Response용 필드 (조회 시에만 사용)
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Request/Response 공통 필드
    private String name;
} 