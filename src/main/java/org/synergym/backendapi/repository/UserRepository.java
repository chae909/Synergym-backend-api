package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    User findUserEntityById(int id);

    boolean existsByEmail(String email);
    boolean existsByName(String name);

    Optional<User> findByNameAndBirthday(String name, LocalDate birthday);
    Optional<User> findByEmailAndName(String email, String name);
}
