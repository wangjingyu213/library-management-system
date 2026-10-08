package com.example.library.service;

import com.example.library.dto.CategoryRequest;
import com.example.library.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category create(CategoryRequest request);
    Category update(Long id, CategoryRequest request);
    void delete(Long id);
}
