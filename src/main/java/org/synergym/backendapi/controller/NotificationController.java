package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.NotificationDTO;
import org.synergym.backendapi.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // 특정 사용자의 알림 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @PathVariable Integer userId,
            Pageable pageable) {
        log.info("사용자 {}의 알림 조회 요청", userId);
        Page<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    // 특정 사용자의 읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Integer userId) {
        log.info("사용자 {}의 읽지 않은 알림 조회 요청", userId);
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Integer userId) {
        log.info("사용자 {}의 읽지 않은 알림 개수 조회 요청", userId);
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }

    // 특정 알림을 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer userId, 
            @PathVariable Integer notificationId) {
        log.info("사용자 {}의 알림 {} 읽음 처리 요청", userId, notificationId);
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // 모든 알림을 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Integer userId) {
        log.info("사용자 {}의 모든 알림 읽음 처리 요청", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 특정 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer userId, 
            @PathVariable Integer notificationId) {
        log.info("사용자 {}의 알림 {} 삭제 요청", userId, notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
