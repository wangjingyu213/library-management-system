package com.example.library.service.impl;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.security.LibraryUserDetails;
import com.example.library.service.BorrowRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public BorrowRecordServiceImpl(BorrowRecordMapper borrowRecordMapper, BookMapper bookMapper, UserMapper userMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<BorrowRecord> findAll(String status, String keyword, Long userId, LibraryUserDetails currentUser) {
        Long effectiveUserId = isAdminOrLibrarian(currentUser) ? userId : currentUser.getUser().getId();
        return borrowRecordMapper.findAll(normalizeStatus(status), normalizeKeyword(keyword), effectiveUserId);
    }

    @Override
    @Transactional
    public BorrowRecord borrow(BorrowRequest request, LibraryUserDetails currentUser) {
        Book book = bookMapper.findById(request.getBookId()).orElseThrow(() -> new IllegalArgumentException("Book not found: " + request.getBookId()));
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No available copies for this book");
        }
        Long borrowerId = isAdminOrLibrarian(currentUser) && request.getUserId() != null ? request.getUserId() : currentUser.getUser().getId();
        userMapper.findById(borrowerId).orElseThrow(() -> new IllegalArgumentException("Borrower not found: " + borrowerId));
        int decreased = bookMapper.decreaseAvailable(book.getId());
        if (decreased == 0) {
            throw new IllegalArgumentException("No available copies for this book");
        }
        BorrowRecord record = new BorrowRecord();
        record.setBookId(book.getId());
        record.setUserId(borrowerId);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(request.getDueDate() == null ? LocalDate.now().plusDays(30) : request.getDueDate());
        record.setRemark(trimToNull(request.getRemark()));
        borrowRecordMapper.insert(record);
        return borrowRecordMapper.findById(record.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long id, ReturnRequest request, LibraryUserDetails currentUser) {
        BorrowRecord record = borrowRecordMapper.findById(id).orElseThrow(() -> new IllegalArgumentException("Borrow record not found: " + id));
        if (!isAdminOrLibrarian(currentUser) && !record.getUserId().equals(currentUser.getUser().getId())) {
            throw new IllegalArgumentException("Cannot return another user's book");
        }
        if (record.getReturnDate() != null) {
            throw new IllegalArgumentException("Book has already been returned");
        }
        int updated = borrowRecordMapper.markReturned(id, LocalDate.now(), trimToNull(request == null ? null : request.getRemark()));
        if (updated == 0) {
            throw new IllegalArgumentException("Book has already been returned");
        }
        bookMapper.increaseAvailable(record.getBookId());
        return borrowRecordMapper.findById(id).orElseThrow();
    }

    private boolean isAdminOrLibrarian(LibraryUserDetails user) {
        return user.getUser().getRoles().contains("ADMIN") || user.getUser().getRoles().contains("LIBRARIAN");
    }

    private String normalizeKeyword(String keyword) {
        String value = trimToNull(keyword);
        return value == null ? null : "%" + value + "%";
    }

    private String normalizeStatus(String status) {
        String value = trimToNull(status);
        return value == null ? null : value.toUpperCase();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }
}
