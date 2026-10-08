package com.example.library.service.impl;

import com.example.library.dto.UserAdminRequest;
import com.example.library.entity.User;
import com.example.library.mapper.UserMapper;
import com.example.library.service.UserAdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAdminServiceImpl implements UserAdminService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAdminServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userMapper.findAll();
        for (User user : users) {
            user.setPasswordHash(null);
            user.setRoles(userMapper.findRolesByUserId(user.getId()));
            user.setPermissions(userMapper.findPermissionsByUserId(user.getId()));
        }
        return users;
    }

    @Override
    @Transactional
    public User create(UserAdminRequest request) {
        validateUsername(request.getUsername(), null);
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for new user");
        }
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setDisplayName(request.getDisplayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        userMapper.insert(user);
        replaceRoles(user.getId(), request.getRoles());
        return loadUser(user.getId());
    }

    @Override
    @Transactional
    public User update(Long id, UserAdminRequest request) {
        User user = userMapper.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        validateUsername(request.getUsername(), id);
        user.setUsername(request.getUsername().trim());
        user.setDisplayName(request.getDisplayName().trim());
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPasswordHash(null);
        }
        userMapper.update(user);
        replaceRoles(id, request.getRoles());
        return loadUser(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userMapper.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Default admin user cannot be deleted");
        }
        userMapper.deleteRoles(id);
        int affected = userMapper.deleteById(id);
        if (affected == 0) {
            throw new IllegalArgumentException("User not found: " + id);
        }
    }

    private User loadUser(Long id) {
        User user = userMapper.findById(id).orElseThrow();
        user.setPasswordHash(null);
        user.setRoles(userMapper.findRolesByUserId(id));
        user.setPermissions(userMapper.findPermissionsByUserId(id));
        return user;
    }

    private void replaceRoles(Long userId, List<String> roles) {
        userMapper.deleteRoles(userId);
        List<String> effectiveRoles = roles == null || roles.isEmpty() ? List.of("READER") : roles;
        for (String role : effectiveRoles) {
            Long roleId = userMapper.findRoleIdByCode(role);
            if (roleId == null) {
                throw new IllegalArgumentException("Unknown role: " + role);
            }
            userMapper.insertRole(userId, roleId);
        }
    }

    private void validateUsername(String username, Long excludeId) {
        String value = username == null ? null : username.trim();
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (userMapper.countByUsername(value, excludeId) > 0) {
            throw new IllegalArgumentException("Username already exists: " + value);
        }
    }
}
