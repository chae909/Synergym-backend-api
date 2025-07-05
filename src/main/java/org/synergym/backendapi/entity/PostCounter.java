package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post_Counters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCounter {

    @Id
    @Column(name = "post_id")
    private Integer postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    public PostCounter(Post post) {
        this.post = post;
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    // Setter 메서드들 (DTO 변환용)
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount != null ? likeCount : 0;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount != null ? commentCount : 0;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount != null ? viewCount : 0;
    }
} 