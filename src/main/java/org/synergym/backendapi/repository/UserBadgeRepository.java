// org/synergym/backendapi/repository/UserBadgeRepository.java
package org.synergym.backendapi.repository;

import org.synergym.backendapi.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {

    // 특정 사용자 ID로 그 사용자가 획득한 모든 UserBadge 정보를 조회
    List<UserBadge> findByUserId(int userId);
}