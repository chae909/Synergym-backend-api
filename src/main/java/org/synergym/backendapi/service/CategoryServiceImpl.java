package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.CategoryDTO;
import org.synergym.backendapi.entity.Category;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;

    // ID로 카테고리 조회 (없으면 예외 발생)
    private Category findCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // 카테고리 생성
    @Override
    @Transactional
    public Integer createCategory(CategoryDTO categoryDTO) {
        Category category = DTOtoEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    // 전체 카테고리 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // ID로 카테고리 조회
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Integer id) {
        Category category = findCategoryById(id);
        return entityToDTO(category);
    }

    // ID로 카테고리 수정
    @Override
    @Transactional
    public void updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category category = findCategoryById(id);
        category.updateName(categoryDTO.getName());
        categoryRepository.save(category);
    }

    // ID로 카테고리 삭제
    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    // 이름으로 카테고리 조회
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
        return entityToDTO(category);
    }

    // 이름 중복 체크
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
} 