package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
