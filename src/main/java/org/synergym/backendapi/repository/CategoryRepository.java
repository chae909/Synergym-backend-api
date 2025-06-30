package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    // 카테고리 이름으로 정확히 찾기
    Optional<Category> findByName(String name);
    
    // 카테고리 이름 존재 여부 확인
    boolean existsByName(String name);
}
