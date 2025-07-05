package org.synergym.backendapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.synergym.backendapi.dto.NotificationDTO;
import org.synergym.backendapi.entity.Notification;
import org.synergym.backendapi.entity.User;

import java.util.List;

public interface NotificationService {

    // 알림 생성
    Integer createNotification(NotificationDTO notificationDTO);

    // 특정 사용자의 알림 조회 (페이징)
    Page<NotificationDTO> getNotificationsByUserId(Integer userId, Pageable pageable);

    // 특정 사용자의 읽지 않은 알림 조회
    List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId);

    // 읽지 않은 알림 개수 조회
    long getUnreadNotificationCount(Integer userId);

    // 알림을 읽음 처리
    void markAsRead(Integer notificationId);

    // 특정 사용자의 모든 알림을 읽음 처리
    void markAllAsRead(Integer userId);

    // 알림 삭제
    void deleteNotification(Integer notificationId);

    // 게시글 좋아요 알림 생성
    void createPostLikeNotification(Integer postId, Integer likerId);

    // 댓글 작성 알림 생성
    void createCommentNotification(Integer postId, Integer commenterId);

    // 운동 좋아요 알림 생성
    void createExerciseLikeNotification(Integer exerciseId, Integer likerId);

    // DTO -> Entity 변환
    default Notification DTOtoEntity(NotificationDTO dto, User user, User sender) {
        return Notification.builder()
                .user(user)
                .sender(sender)
                .type(dto.getType())
                .message(dto.getMessage())
                .referenceId(dto.getReferenceId())
                .build();
    }

    // Entity -> DTO 변환
    default NotificationDTO entityToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getName())
                .senderId(notification.getSender().getId())
                .senderName(notification.getSender().getName())
                .type(notification.getType())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .useYn(notification.getUseYn())
                .build();
    }
}
