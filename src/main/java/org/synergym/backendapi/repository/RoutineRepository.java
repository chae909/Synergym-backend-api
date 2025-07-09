package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.User;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
    // 루틴 이름으로 검색
    List<Routine> findByName(String name);
    // 사용자별 루틴 조회
    List<Routine> findByUser(User user);
}