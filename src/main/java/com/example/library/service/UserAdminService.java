package com.example.library.service;

import com.example.library.dto.UserAdminRequest;
import com.example.library.entity.User;
import java.util.List;

public interface UserAdminService {
    List<User> findAll();
    User create(UserAdminRequest request);
    User update(Long id, UserAdminRequest request);
    void delete(Long id);
}
