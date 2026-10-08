package com.example.library.dto;

import java.util.List;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String displayName;
    private List<String> roles;
    private List<String> permissions;

    public LoginResponse(String token, String username, String displayName, List<String> roles, List<String> permissions) {
        this.token = token;
        this.username = username;
        this.displayName = displayName;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
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

