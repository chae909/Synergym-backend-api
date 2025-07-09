package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 게시글 집계 데이터 엔티티
 * 게시글의 좋아요 수, 댓글 수, 조회 수를 빠른 조회를 위해 별도 저장
 * 
 * [PostLike와의 차이점]
 * - PostLike: "누가 좋아요를 눌렀는지" 개별 관계 기록
 * - PostCounter: "몇 명이 좋아요를 눌렀는지" 집계 숫자만 저장
 */
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
    private Integer likeCount = 0;  // 좋아요 수 (집계)

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0; // 댓글 수 (집계)

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0; // 조회 수 (집계)

    // 생성자
    public PostCounter(Post post) {
        this.post = post;
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
    }

    // 좋아요 수 증가
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 댓글 수 증가
    public void incrementCommentCount() {
        this.commentCount++;
    }

    // 댓글 수 감소
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    // 조회 수 증가
    public void incrementViewCount() {
        this.viewCount++;
    }

    // Setter 메서드들 (DTO 변환용)
    // 좋아요 수 설정
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount != null ? likeCount : 0;
    }

    // 댓글 수 설정
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount != null ? commentCount : 0;
    }

    // 조회 수 설정
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount != null ? viewCount : 0;
    }
} 