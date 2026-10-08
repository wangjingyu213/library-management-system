package com.example.library.controller;

import com.example.library.dto.BorrowRequest;
import com.example.library.dto.ReturnRequest;
import com.example.library.entity.BorrowRecord;
import com.example.library.security.LibraryUserDetails;
import com.example.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {
    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'READER')")
    public List<BorrowRecord> list(@RequestParam(required = false) String status,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Long userId,
                                   @AuthenticationPrincipal LibraryUserDetails currentUser) {
        return borrowRecordService.findAll(status, keyword, userId, currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'READER')")
    public BorrowRecord borrow(@Valid @RequestBody BorrowRequest request, @AuthenticationPrincipal LibraryUserDetails currentUser) {
        return borrowRecordService.borrow(request, currentUser);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'READER')")
    public BorrowRecord returnBook(@PathVariable Long id,
                                   @RequestBody(required = false) ReturnRequest request,
                                   @AuthenticationPrincipal LibraryUserDetails currentUser) {
        return borrowRecordService.returnBook(id, request, currentUser);
    }
}
