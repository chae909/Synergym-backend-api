package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post_Likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

/*
 * 게시글 좋아요 관계 엔티티
 * "누가 어떤 게시글에 좋아요를 눌렀는지" 개별 관계를 기록
 * 
 * [PostCounter와의 차이점]
 * - PostLike: 개별 사용자-게시글 관계 기록 (중복 방지, 이력 관리)
 * - PostCounter: 집계된 숫자만 저장 (빠른 조회용)
 */
public class PostLike {

    @EmbeddedId
    private PostLikeId id; // 복합키 (userId + postId)

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;  // 좋아요를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;  // 좋아요를 받은 게시글

    @Builder
    public PostLike(User user, Post post) {
        this.id = new PostLikeId(user.getId(), post.getId());
        this.user = user;
        this.post = post;
    }
}
