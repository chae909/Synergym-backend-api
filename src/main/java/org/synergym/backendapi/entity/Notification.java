package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 알림을 받을 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // 알림을 발생시킨 사용자

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "reference_id")
    private Integer referenceId;  // 관련된 게시글, 댓글 등의 ID

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(User user, User sender, NotificationType type, String message, Integer referenceId) {
        this.user = user;
        this.sender = sender;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public enum NotificationType {
        POST_LIKE,      // 게시글 좋아요
        POST_COMMENT,   // 게시글 댓글
        EXERCISE_LIKE   // 운동 좋아요
    }
}
