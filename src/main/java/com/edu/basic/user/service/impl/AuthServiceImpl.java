package com.edu.basic.user.service.impl;

import com.edu.basic.Security.JwtProvider;
import com.edu.basic.user.dto.request.LoginRequest;
import com.edu.basic.user.dto.request.RegisterRequest;
import com.edu.basic.user.dto.response.AuthResponse;
import com.edu.basic.user.dto.response.UserResponse;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.mapper.UserMapper;
import com.edu.basic.user.repositary.UserRepository;
import com.edu.basic.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {

        // Check if email already exists
        if (emailExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(null, null, "Email already registered"));
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setRole("USER");
        user.setIsActive(true);
        user.setIsEmailVerified(false);

        // Save user to database
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail());

        // Map to response
        UserResponse userResponse = userMapper.mapEntityToResponse(savedUser);

        AuthResponse authResponse = new AuthResponse(
                token,
                userResponse,
                "User registered successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid email or password"));
        }

        // Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid email or password"));
        }

        // Check if user is active
        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "User account is inactive"));
        }

        // Generate JWT token
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());

        // Map to response
        UserResponse userResponse = userMapper.mapEntityToResponse(user);

        AuthResponse authResponse = new AuthResponse(
                token,
                userResponse,
                "Login successful"
        );

        return ResponseEntity.ok(authResponse);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}