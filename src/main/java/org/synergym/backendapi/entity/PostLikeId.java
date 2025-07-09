package org.synergym.backendapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

/*
 * 게시글 좋아요 복합키
 * 사용자 ID + 게시글 ID 조합으로 중복 좋아요 방지
 */
public class PostLikeId implements Serializable {
    private Integer userId; // 좋아요를 누른 사용자 ID
    private Integer postId; // 좋아요를 받은 게시글 ID
}