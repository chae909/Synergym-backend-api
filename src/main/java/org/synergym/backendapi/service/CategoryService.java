package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.CategoryDTO;
import org.synergym.backendapi.entity.Category;

import java.util.List;

public interface CategoryService {

    // 카테고리 생성
    Integer createCategory(CategoryDTO categoryDTO);

    // 모든 카테고리 조회
    List<CategoryDTO> getAllCategories();

    // ID로 카테고리 조회
    CategoryDTO getCategoryById(Integer id);

    // 카테고리 수정
    void updateCategory(Integer id, CategoryDTO categoryDTO);

    // 카테고리 삭제
    void deleteCategory(Integer id);


    // 카테고리 이름으로 정확히 찾기
    CategoryDTO getCategoryByName(String name);

    // 카테고리 이름 존재 여부 확인(카테고리 생성 시 중복 체크)
    boolean existsByName(String name);

    // DTO -> Entity 변환
    default Category DTOtoEntity(CategoryDTO dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    // Entity -> DTO 변환
    default CategoryDTO entityToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
} 