package com.edu.basic.user.service;

import com.edu.basic.user.dto.request.LoginRequest;
import com.edu.basic.user.dto.request.RegisterRequest;
import com.edu.basic.user.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<AuthResponse> register(RegisterRequest request);

    ResponseEntity<AuthResponse> login(LoginRequest request);

    boolean emailExists(String email);
}