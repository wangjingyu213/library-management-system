package com.example.library.mapper;

import com.example.library.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryMapper {
    List<Category> findAll();
    Optional<Category> findById(@Param("id") Long id);
    int insert(Category category);
    int update(Category category);
    int deleteById(@Param("id") Long id);
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);
    int countBooks(@Param("id") Long id);
}
