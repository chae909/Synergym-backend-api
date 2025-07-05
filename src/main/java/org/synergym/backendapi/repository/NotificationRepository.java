package org.synergym.backendapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.synergym.backendapi.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 사용자별 알림 조회 (페이징, 최신순)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    // 사용자별 읽지 않은 알림 조회
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);

    // 사용자별 읽지 않은 알림 개수 조회
    long countByUserIdAndIsReadFalse(Integer userId);

    // 사용자별 알림 전체 개수 조회
    long countByUserId(Integer userId);

    // 특정 사용자의 모든 알림을 읽음 처리
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Integer userId);
}
