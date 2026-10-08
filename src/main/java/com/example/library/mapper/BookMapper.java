package com.example.library.mapper;

import com.example.library.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface BookMapper {
    List<Book> findAll(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("stockStatus") String stockStatus);
    Optional<Book> findById(@Param("id") Long id);
    int insert(Book book);
    int update(Book book);
    int deleteById(@Param("id") Long id);
    int countByIsbn(@Param("isbn") String isbn, @Param("excludeId") Long excludeId);
    int decreaseAvailable(@Param("id") Long id);
    int increaseAvailable(@Param("id") Long id);
}
