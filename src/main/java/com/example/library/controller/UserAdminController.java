package com.example.library.controller;

import com.example.library.dto.UserAdminRequest;
import com.example.library.entity.User;
import com.example.library.service.UserAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<User> list() {
        return userAdminService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserAdminRequest request) {
        return userAdminService.create(request);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserAdminRequest request) {
        return userAdminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userAdminService.delete(id);
    }
}
