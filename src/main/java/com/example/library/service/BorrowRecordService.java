package com.example.library.service;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.entity.BorrowRecord;
import com.example.library.security.LibraryUserDetails;

import java.util.List;

public interface BorrowRecordService {
    List<BorrowRecord> findAll(String status, String keyword, Long userId, LibraryUserDetails currentUser);
    BorrowRecord borrow(BorrowRequest request, LibraryUserDetails currentUser);
    BorrowRecord returnBook(Long id, ReturnRequest request, LibraryUserDetails currentUser);
}
