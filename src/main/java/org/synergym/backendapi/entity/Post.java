package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostCounter postCounter;

    @Builder
    public Post(User user, Category category, String title, String content, String imageUrl) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void updateCategory(Category newCategory) {
        this.category = newCategory;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void updateImageUrl(String newImageUrl) {
        this.imageUrl = newImageUrl;
    }

    // PostCounter를 통한 카운터 조회 메서드들 (안정적인 버전)
    public int getLikeCount() {
        try {
            return postCounter != null ? postCounter.getLikeCount() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCommentCount() {
        try {
            // PostCounter가 있으면 PostCounter의 값 사용
            if (postCounter != null) {
                return postCounter.getCommentCount();
            }
            // PostCounter가 없으면 실제 댓글 수 반환
            return comments != null ? comments.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getViewCount() {
        try {
            return postCounter != null ? postCounter.getViewCount() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // PostCounter 초기화 (안정적인 버전)
    public void initializeCounter() {
        try {
            if (this.postCounter == null) {
                this.postCounter = new PostCounter(this);
            }
        } catch (Exception e) {
            // 초기화 실패 시 무시하고 계속 진행
        }
    }
}