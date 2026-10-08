package com.example.library.controller;

import com.example.library.dto.LoginRequest;
import com.example.library.dto.LoginResponse;
import com.example.library.dto.UserProfileResponse;
import com.example.library.security.JwtTokenService;
import com.example.library.security.LibraryUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        LibraryUserDetails userDetails = (LibraryUserDetails) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return new LoginResponse(
                token,
                userDetails.getUser().getUsername(),
                userDetails.getUser().getDisplayName(),
                userDetails.getUser().getRoles(),
                userDetails.getUser().getPermissions());
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        LibraryUserDetails userDetails = (LibraryUserDetails) authentication.getPrincipal();
        return new UserProfileResponse(userDetails.getUser());
    }
}

