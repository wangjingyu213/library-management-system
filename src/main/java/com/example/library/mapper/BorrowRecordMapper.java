package com.example.library.mapper;

import com.example.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordMapper {
    List<BorrowRecord> findAll(@Param("status") String status, @Param("keyword") String keyword, @Param("userId") Long userId);
    Optional<BorrowRecord> findById(@Param("id") Long id);
    int insert(BorrowRecord record);
    int markReturned(@Param("id") Long id, @Param("returnDate") LocalDate returnDate, @Param("remark") String remark);
    int countActiveByBookId(@Param("bookId") Long bookId);
    int countByBookId(@Param("bookId") Long bookId);
}

