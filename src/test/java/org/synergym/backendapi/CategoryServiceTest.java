package org.synergym.backendapi;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.CategoryDTO;
import org.synergym.backendapi.service.CategoryService;

import java.util.List;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    @Order(1)
    @DisplayName("카테고리 생성 테스트")
    void createCategory() throws Exception {
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("자유게시판")
                .build();

        Integer categoryId = categoryService.createCategory(newCategoryDTO);
        System.out.println("Created category ID: " + categoryId);
        
        // 생성된 카테고리 확인
        CategoryDTO createdCategory = categoryService.getCategoryById(categoryId);
        System.out.println("Created category: " + createdCategory);
    }

    @Test
    @Order(2)
    @DisplayName("전체 카테고리 조회 테스트")
    void getAllCategories() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("공지사항")
                .build();
        categoryService.createCategory(newCategoryDTO);
        
        List<CategoryDTO> categories = categoryService.getAllCategories();
        System.out.println("All categories: " + categories);
    }

    @Test
    @Order(3)
    @DisplayName("ID로 카테고리 조회 테스트")
    void getCategoryById() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("질문게시판")
                .build();
        Integer categoryId = categoryService.createCategory(newCategoryDTO);
        
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        System.out.println("Category by ID: " + category);
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 카테고리 조회 테스트")
    void getCategoryByName() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("자유게시판")
                .build();
        categoryService.createCategory(newCategoryDTO);
        
        CategoryDTO category = categoryService.getCategoryByName("자유게시판");
        System.out.println("Category by name: " + category);
    }

    @Test
    @Order(5)
    @DisplayName("카테고리 수정 테스트")
    void updateCategory() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("원본게시판")
                .build();
        Integer categoryId = categoryService.createCategory(newCategoryDTO);
        
        CategoryDTO updateInfo = CategoryDTO.builder()
                .name("수정된게시판")
                .build();

        categoryService.updateCategory(categoryId, updateInfo);
        System.out.println("Category updated successfully");
        
        // 수정 확인
        CategoryDTO updatedCategory = categoryService.getCategoryById(categoryId);
        System.out.println("Updated category: " + updatedCategory);
    }

    @Test
    @Order(6)
    @DisplayName("카테고리 존재 여부 확인 테스트")
    void existsByName() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("테스트게시판")
                .build();
        categoryService.createCategory(newCategoryDTO);
        
        boolean exists = categoryService.existsByName("테스트게시판");
        System.out.println("Category exists: " + exists);
        
        boolean notExists = categoryService.existsByName("존재하지않는게시판");
        System.out.println("Non-existent category: " + notExists);
    }

    @Test
    @Order(7)
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategory() throws Exception {
        // 먼저 카테고리 생성
        CategoryDTO newCategoryDTO = CategoryDTO.builder()
                .name("삭제될게시판")
                .build();
        Integer categoryId = categoryService.createCategory(newCategoryDTO);
        
        categoryService.deleteCategory(categoryId);
        System.out.println("Category deleted successfully");
    }
} 