package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.NotificationDTO;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.service.NotificationService;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;    // 사용자별 알림 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @PathVariable Integer userId,
            Pageable pageable) {
        try {
            
            // 사용자 ID 유효성 검증
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            Page<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId, pageable);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    // 특정 사용자의 읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Integer userId) {
        try {
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Integer userId) {
        try {
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            long count = notificationService.getUnreadNotificationCount(userId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    // 특정 알림을 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer userId, 
            @PathVariable Integer notificationId) {
        try {
            
            if (userId == null || userId <= 0 || notificationId == null || notificationId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    // 모든 알림을 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Integer userId) {
        try {
            
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    // 특정 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer userId, 
            @PathVariable Integer notificationId) {
        try {
            
            if (userId == null || userId <= 0 || notificationId == null || notificationId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
