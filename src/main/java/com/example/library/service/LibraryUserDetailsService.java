package com.example.library.service;

import com.example.library.entity.User;
import com.example.library.mapper.UserMapper;
import com.example.library.security.LibraryUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LibraryUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public LibraryUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        user.setRoles(userMapper.findRolesByUserId(user.getId()));
        user.setPermissions(userMapper.findPermissionsByUserId(user.getId()));
        return new LibraryUserDetails(user);
    }
}

