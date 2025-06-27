package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Exercise;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    
    // 운동 이름으로 검색 (부분 일치)
    List<Exercise> findByNameContaining(String name);
    
    // 운동 카테고리별 조회
    List<Exercise> findByCategory(String category);
}
