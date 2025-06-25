package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
