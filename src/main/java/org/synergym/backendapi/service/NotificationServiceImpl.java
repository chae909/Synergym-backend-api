package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.NotificationDTO;
import org.synergym.backendapi.entity.*;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 사용자 ID로 User 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        log.debug("사용자 조회 완료: ID={}, 이름={}", user.getId(), user.getName());
        return user;
    }

    // 알림 ID로 Notification 조회 (없으면 예외 발생)
    private Notification findNotificationById(int id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    /**
     * 알림 생성
     */
    @Override
    @Transactional
    public NotificationDTO createNotification(User user, String title, String message, Notification.NotificationType type, User sender) {
        log.info("알림 생성 시작: 사용자 ID={}, 타입={}, 메시지={}", user.getId(), type, message);
        
        Notification notification = Notification.builder()
                .user(user)
                .sender(sender)
                .type(type)
                .message(message)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("알림 저장 완료: ID={}", savedNotification.getId());

        if (sender != null) {
            log.info("알림 생성됨: 발신자 ID {} -> 수신자 ID {}, 타입: {}",
                    sender.getId(), user.getId(), type);
        } else {
            log.info("시스템 알림 생성됨: 수신자 ID {}, 타입: {}", user.getId(), type);
        }

        return entityToDTO(savedNotification);
    }
    
    /**
     * 특정 사용자에 대한 알림 목록 조회 (페이지네이션)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserId(Integer userId, Pageable pageable) {
        log.info("=== 알림 조회 서비스 시작 ===");
        log.info("사용자 ID: {}", userId);
        log.info("페이지 정보: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // 사용자 존재 확인
            User user = findUserById(userId);
            log.info("사용자 확인 완료: {}", user.getName());
            
            // 알림 조회
            Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
            log.info("DB 조회 완료: 총 {}개의 알림", notifications.getTotalElements());
            
            Page<NotificationDTO> result = notifications.map(this::entityToDTO);
            log.info("=== 알림 조회 서비스 완료 ===");
            return result;
            
        } catch (Exception e) {
            log.error("=== 알림 조회 서비스 실패 ===", e);
            throw e;
        }
    }

    /**
     * 읽지 않은 알림 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId) {
        log.info("읽지 않은 알림 조회 시작: 사용자 ID={}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        log.info("읽지 않은 알림 조회 완료: {}개", notifications.size());
        
        return notifications.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Integer userId) {
        log.info("읽지 않은 알림 개수 조회: 사용자 ID={}", userId);
        
        long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        log.info("읽지 않은 알림 개수: {}", count);
        
        return count;
    }

    /**
     * 특정 알림을 읽음 처리
     */
    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        log.info("알림 읽음 처리 시작: 알림 ID={}", notificationId);
        
        Notification notification = findNotificationById(notificationId);
        notification.markAsRead();
        notificationRepository.save(notification);
        
        log.info("알림 읽음 처리 완료: 알림 ID={}", notificationId);
    }

    /**
     * 사용자의 모든 알림을 읽음 처리
     */
    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        log.info("모든 알림 읽음 처리 시작: 사용자 ID={}", userId);
        
        notificationRepository.markAllAsReadByUserId(userId);
        
        log.info("모든 알림 읽음 처리 완료: 사용자 ID={}", userId);
    }

    /**
     * 특정 알림 삭제
     */
    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        log.info("알림 삭제 시작: 알림 ID={}", notificationId);
        
        Notification notification = findNotificationById(notificationId);
        notificationRepository.delete(notification);
        
        log.info("알림 삭제 완료: 알림 ID={}", notificationId);
    }

    /**
     * 게시글 좋아요 알림 생성
     */
    @Override
    @Transactional
    public void createPostLikeNotification(Integer postId, Integer likerId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
            User postOwner = post.getUser();
            User liker = userRepository.findById(likerId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

            // 자신의 게시글에 좋아요를 누르면 알림을 생성하지 않음
            if (postOwner.getId() == likerId) {
                return;
            }

            String title = "새로운 좋아요!";
            String message = String.format("'%s'님이 회원님의 게시글에 좋아요를 눌렀습니다.", liker.getName());

            // 리팩터링된 createNotification 메소드 호출
            createNotification(postOwner, title, message, Notification.NotificationType.POST_LIKE, liker);

        } catch (Exception e) {
            log.error("게시글 좋아요 알림 생성 실패: postId={}, likerId={}", postId, likerId, e);
        }
    }

    /**
     * 게시글 댓글 알림 생성
     */
    @Override
    @Transactional
    public void createCommentNotification(Integer postId, Integer commenterId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
            User postOwner = post.getUser();
            User commenter = userRepository.findById(commenterId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

            // 자신의 게시글에 댓글을 달면 알림을 생성하지 않음
            if (postOwner.getId() == commenterId) {
                return;
            }

            String title = "새로운 댓글이 달렸어요";
            String message = String.format("'%s'님이 회원님의 게시글에 댓글을 달았습니다.", commenter.getName());

            // 리팩터링된 createNotification 메소드 호출
            createNotification(postOwner, title, message, Notification.NotificationType.POST_COMMENT, commenter);

        } catch (Exception e) {
            log.error("댓글 알림 생성 실패: postId={}, commenterId={}", postId, commenterId, e);
        }
    }
}
