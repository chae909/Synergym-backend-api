package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * 특정 날짜 이후에 활동(업데이트)한 사용자 수 조회
     * @param date 기준 날짜
     * @return 사용자 수
     */
    long countByUpdatedAtAfter(LocalDateTime date);

    /**
     * 두 날짜 사이에 활동(업데이트)한 사용자 수 조회
     * @param start 시작 날짜
     * @param end 종료 날짜
     * @return 사용자 수
     */
    long countByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);
}