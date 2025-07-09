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

    // 사용자 ID로 User 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
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
    public Integer createNotification(NotificationDTO notificationDTO) {
        User user = findUserById(notificationDTO.getUserId());       // 알림 수신자
        User sender = findUserById(notificationDTO.getSenderId());   // 알림 발신자

        Notification notification = DTOtoEntity(notificationDTO, user, sender);
        Notification savedNotification = notificationRepository.save(notification);

        log.info("알림 생성됨: 사용자 ID {} -> 사용자 ID {}, 타입: {}",
                sender.getId(), user.getId(), notificationDTO.getType());

        return savedNotification.getId();
    }

    /**
     * 특정 사용자에 대한 알림 목록 조회 (페이지네이션)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserId(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::entityToDTO);
    }

    /**
     * 읽지 않은 알림 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 특정 알림을 읽음 처리
     */
    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = findNotificationById(notificationId);
        notification.markAsRead();                     // 읽음 상태 변경
        notificationRepository.save(notification);     // 변경사항 저장
    }

    /**
     * 사용자의 모든 알림을 읽음 처리
     */
    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    /**
     * 특정 알림 삭제
     */
    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        Notification notification = findNotificationById(notificationId);
        notificationRepository.delete(notification);
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
            User liker = findUserById(likerId);

            // 자신의 게시글에 좋아요를 눌렀다면 알림 생성 안 함
            if (postOwner.getId() == likerId) return;

            // 게시글 제목이 길면 20자까지만 표시
            String message = String.format("%s님이 회원님의 게시글 '%s'에 좋아요를 눌렀습니다.",
                    liker.getName(),
                    post.getTitle().length() > 20 ? post.getTitle().substring(0, 20) + "..." : post.getTitle());

            // 알림 DTO 생성 후 알림 생성
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
            User commenter = findUserById(commenterId);

            // 자신의 게시글에 댓글을 단 경우 알림 생성 안 함
            if (postOwner.getId() == commenterId) return;

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

    /**
     * 운동 좋아요 알림 생성 (현재는 동작하지 않음 - 향후 기능 확장 시 사용)
     */
    @Override
    @Transactional
    public void createExerciseLikeNotification(Integer exerciseId, Integer likerId) {
        try {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));

            // 현재는 알림을 전송할 대상이 없음 (운동 owner 기능이 구현되어 있지 않음)
            log.info("운동 좋아요 알림: exerciseId={}, likerId={}, exerciseName={}",
                    exerciseId, likerId, exercise.getName());

        } catch (Exception e) {
            log.error("운동 좋아요 알림 생성 실패: exerciseId={}, likerId={}", exerciseId, likerId, e);
        }
    }
}
