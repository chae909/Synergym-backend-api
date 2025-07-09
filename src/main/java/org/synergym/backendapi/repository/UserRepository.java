package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이름에 특정 문자열이 포함된 사용자 목록 조회 (부분 일치 검색)
    List<User> findByNameContaining(String name);

    // ID로 사용자 엔티티 직접 조회 (Optional 아님 → 결과 없으면 예외 발생 가능)
    User findUserEntityById(int id);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 이름 존재 여부 확인
    boolean existsByName(String name);

    // 이름 + 생년일로 사용자 조회 (예: 아이디 찾기용)
    Optional<User> findByNameAndBirthday(String name, LocalDate birthday);

    // 이메일 + 이름으로 사용자 조회 (예: 비밀번호 찾기용)
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