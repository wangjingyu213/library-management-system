package com.example.library.dto;

import com.example.library.entity.User;

import java.util.List;

public class UserProfileResponse {

    private String username;
    private String displayName;
    private List<String> roles;
    private List<String> permissions;

    public UserProfileResponse(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.roles = user.getRoles();
        this.permissions = user.getPermissions();
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}

