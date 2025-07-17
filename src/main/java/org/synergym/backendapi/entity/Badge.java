// org/synergym/backendapi/entity/Badge.java
package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // 뱃지 이름 (예: 허리교정 달인)

    @Column(length = 512)
    private String description; // 뱃지 설명 (AI가 생성)

    @Builder
    public Badge(String name, String description) {
        this.name = name;
        this.description = description;
    }
}