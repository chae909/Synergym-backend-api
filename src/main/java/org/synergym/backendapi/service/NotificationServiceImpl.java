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
    private final ExerciseRepository exerciseRepository;

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Notification findNotificationById(int id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Override
    @Transactional
    public Integer createNotification(NotificationDTO notificationDTO) {
        User user = findUserById(notificationDTO.getUserId());
        User sender = findUserById(notificationDTO.getSenderId());

        Notification notification = DTOtoEntity(notificationDTO, user, sender);
        Notification savedNotification = notificationRepository.save(notification);
        
        log.info("알림 생성됨: 사용자 ID {} -> 사용자 ID {}, 타입: {}", 
                sender.getId(), user.getId(), notificationDTO.getType());
        
        return savedNotification.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserId(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = findNotificationById(notificationId);
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        Notification notification = findNotificationById(notificationId);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void createPostLikeNotification(Integer postId, Integer likerId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
            User postOwner = post.getUser();
            User liker = userRepository.findById(likerId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

            // 자신의 게시글에 좋아요를 눌렀을 경우 알림을 보내지 않음
            if (postOwner.getId() == likerId) {
                return;
            }

            String message = String.format("%s님이 회원님의 게시글 '%s'에 좋아요를 눌렀습니다.", 
                    liker.getName(), 
                    post.getTitle().length() > 20 ? post.getTitle().substring(0, 20) + "..." : post.getTitle());

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .userId(postOwner.getId())
                    .senderId(likerId)
                    .type(Notification.NotificationType.POST_LIKE)
                    .message(message)
                    .referenceId(postId)
                    .build();

            createNotification(notificationDTO);
        } catch (Exception e) {
            log.error("게시글 좋아요 알림 생성 실패: postId={}, likerId={}", postId, likerId, e);
        }
    }

    @Override
    @Transactional
    public void createCommentNotification(Integer postId, Integer commenterId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND));
            User postOwner = post.getUser();
            User commenter = userRepository.findById(commenterId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

            // 자신의 게시글에 댓글을 달았을 경우 알림을 보내지 않음
            if (postOwner.getId() == commenterId) {
                return;
            }

            String message = String.format("%s님이 회원님의 게시글 '%s'에 댓글을 달았습니다.", 
                    commenter.getName(), 
                    post.getTitle().length() > 20 ? post.getTitle().substring(0, 20) + "..." : post.getTitle());

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .userId(postOwner.getId())
                    .senderId(commenterId)
                    .type(Notification.NotificationType.POST_COMMENT)
                    .message(message)
                    .referenceId(postId)
                    .build();

            createNotification(notificationDTO);
        } catch (Exception e) {
            log.error("댓글 알림 생성 실패: postId={}, commenterId={}", postId, commenterId, e);
        }
    }

    @Override
    @Transactional
    public void createExerciseLikeNotification(Integer exerciseId, Integer likerId) {
        try {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));

            // 운동 좋아요의 경우 시스템 관리자나 운동 등록자에게 알림을 보낼 수 있지만,
            // 현재는 운동에 owner가 없으므로 이 기능은 향후 확장 시 구현할 수 있음
            log.info("운동 좋아요 알림: exerciseId={}, likerId={}, exerciseName={}", 
                    exerciseId, likerId, exercise.getName());
            
        } catch (Exception e) {
            log.error("운동 좋아요 알림 생성 실패: exerciseId={}, likerId={}", exerciseId, likerId, e);
        }
    }
}
