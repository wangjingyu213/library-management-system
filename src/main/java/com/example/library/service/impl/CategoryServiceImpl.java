package com.example.library.service.impl;

import com.example.library.dto.CategoryRequest;
import com.example.library.entity.Category;
import com.example.library.mapper.CategoryMapper;
import com.example.library.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    @Override
    @Transactional
    public Category create(CategoryRequest request) {
        validateName(request.getName(), null);
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(trimToNull(request.getDescription()));
        categoryMapper.insert(category);
        return categoryMapper.findById(category.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = categoryMapper.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        validateName(request.getName(), id);
        category.setName(request.getName().trim());
        category.setDescription(trimToNull(request.getDescription()));
        categoryMapper.update(category);
        return categoryMapper.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (categoryMapper.countBooks(id) > 0) {
            throw new IllegalArgumentException("Category is used by books and cannot be deleted");
        }
        int affected = categoryMapper.deleteById(id);
        if (affected == 0) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
    }

    private void validateName(String name, Long excludeId) {
        String value = name == null ? null : name.trim();
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be blank");
        }
        if (categoryMapper.countByName(value, excludeId) > 0) {
            throw new IllegalArgumentException("Category name already exists: " + value);
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }
}
