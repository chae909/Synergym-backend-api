package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.synergym.backendapi.entity.Badge;

import java.time.LocalDateTime;

@Getter
public class BadgeDTO {

    private final Integer id;

    @JsonProperty("badge_name")
    private final String name;

    @JsonProperty("badge_description")
    private final String description;

    private final String imageUrl;
    private final LocalDateTime createdAt;

    @Builder
    public BadgeDTO(Integer id, String name, String description, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    // Badge 엔티티를 BadgeDTO로 변환하는 정적 팩토리 메서드
    public static BadgeDTO fromEntity(Badge badge) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .createdAt(badge.getCreatedAt()) // BaseEntity의 createdAt 사용
                .build();
    }
}