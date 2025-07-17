// org/synergym/backendapi/repository/UserBadgeRepository.java
package org.synergym.backendapi.repository;

import org.synergym.backendapi.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
}