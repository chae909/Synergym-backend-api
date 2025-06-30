package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.CategoryDTO;
import org.synergym.backendapi.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 단일 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<Integer> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Integer id = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(id);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.noContent().build();
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // 이름으로 카테고리 조회
    @GetMapping("/search")
    public ResponseEntity<CategoryDTO> getCategoryByName(@RequestParam String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    // 이름 중복 체크
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        return ResponseEntity.ok(categoryService.existsByName(name));
    }
}