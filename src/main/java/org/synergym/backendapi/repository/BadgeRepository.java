// org/synergym/backendapi/repository/BadgeRepository.java
package org.synergym.backendapi.repository;

import org.synergym.backendapi.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
}