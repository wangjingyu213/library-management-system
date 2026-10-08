package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> findAll(String keyword, Long categoryId, String stockStatus);
    Book findById(Long id);
    Book create(BookRequest request);
    Book update(Long id, BookRequest request);
    void delete(Long id);
}
