package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.synergym.backendapi.entity.Notification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 알림 관련 DTO
public class NotificationDTO {

    // Response용 필드
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Character useYn;

    // Request/Response 공통 필드
    private Integer userId;        // 알림받을 사용자 ID
    private String userName;       // 알림받을 사용자 이름 (Response용)
    private Integer senderId;      // 알림을 발생시킨 사용자 ID
    private String senderName;     // 알림을 발생시킨 사용자 이름 (Response용)
    private Notification.NotificationType type;  // 알림 타입
    private String message;        // 알림 메시지
    private Integer referenceId;   // 관련된 게시글, 댓글 등의 ID
    private boolean isRead;        // 읽음 여부
}
