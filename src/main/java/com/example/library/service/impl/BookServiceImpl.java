package com.example.library.service.impl;

import com.example.library.dto.BookRequest;
import com.example.library.entity.Book;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public BookServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    public List<Book> findAll(String keyword, Long categoryId, String stockStatus) {
        return bookMapper.findAll(normalizeKeyword(keyword), categoryId, trimToNull(stockStatus));
    }

    @Override
    public Book findById(Long id) {
        return bookMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    @Override
    @Transactional
    public Book create(BookRequest request) {
        validateCopies(request);
        validateIsbn(request.getIsbn(), null);
        Book book = toBook(new Book(), request);
        bookMapper.insert(book);
        return findById(book.getId());
    }

    @Override
    @Transactional
    public Book update(Long id, BookRequest request) {
        validateCopies(request);
        validateIsbn(request.getIsbn(), id);
        Book book = findById(id);
        toBook(book, request);
        bookMapper.update(book);
        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (borrowRecordMapper.countByBookId(id) > 0) {
            throw new IllegalArgumentException("Book has borrow records and cannot be deleted");
        }
        int affected = bookMapper.deleteById(id);
        if (affected == 0) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
    }

    private Book toBook(Book book, BookRequest request) {
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setIsbn(trimToNull(request.getIsbn()));
        book.setPublisher(trimToNull(request.getPublisher()));
        book.setPublishYear(request.getPublishYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setCategoryId(request.getCategoryId());
        book.setShelfLocation(trimToNull(request.getShelfLocation()));
        book.setSummary(trimToNull(request.getSummary()));
        book.setCoverUrl(trimToNull(request.getCoverUrl()));
        return book;
    }

    private void validateCopies(BookRequest request) {
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
    }

    private void validateIsbn(String isbn, Long excludeId) {
        String normalizedIsbn = trimToNull(isbn);
        if (normalizedIsbn != null && bookMapper.countByIsbn(normalizedIsbn, excludeId) > 0) {
            throw new IllegalArgumentException("ISBN already exists: " + normalizedIsbn);
        }
    }

    private String normalizeKeyword(String keyword) {
        String value = trimToNull(keyword);
        return value == null ? null : "%" + value + "%";
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }
}
