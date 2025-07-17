// org/synergym/backendapi/service/AchievementServiceImpl.java
package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.entity.Badge;
import org.synergym.backendapi.entity.Notification;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.entity.UserBadge;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.BadgeRepository;
import org.synergym.backendapi.repository.UserBadgeRepository;
import org.synergym.backendapi.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void awardBadgeToUser(int userId, String badgeName, String badgeDescription) {
        // 1. 사용자 조회 먼저 수행
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 새로운 뱃지 정보 생성 및 저장
        Badge newBadge = Badge.builder()
                .name(badgeName)
                .description(badgeDescription)
                .build();
        badgeRepository.save(newBadge);

        // 3. 사용자와 뱃지 연결 기록 생성
        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(newBadge)
                .build();
        userBadgeRepository.save(userBadge);

        // 4. 알림 생성을 별도의 try-catch로 감싸 안정성 확보
        // 알림 실패가 뱃지 수여의 성공에 영향을 주지 않도록 합니다.
        try {
            String notificationTitle = "새 뱃지 획득: " + badgeName;
            notificationService.createNotification(user, notificationTitle, badgeDescription, Notification.NotificationType.BADGE, user);

        } catch (Exception e) {
            log.error("전체 예외 스택 트레이스: ", e);
        }
    }
}
