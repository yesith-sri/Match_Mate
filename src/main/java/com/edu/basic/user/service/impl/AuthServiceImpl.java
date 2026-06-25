package com.edu.basic.user.service.impl;

import com.edu.basic.Security.JwtProvider;
import com.edu.basic.exception.BusinessException;
import com.edu.basic.exception.ErrorCode;
import com.edu.basic.exception.UnauthorizedException;
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

        if (emailExists(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                    "Email already registered: " + request.getEmail());
        }

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

        User savedUser = userRepository.save(user);
        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail());
        UserResponse userResponse = userMapper.mapEntityToResponse(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, userResponse, "User registered successfully"));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.INVALID_CREDENTIALS, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new UnauthorizedException(ErrorCode.USER_DISABLED, "User account is inactive");
        }

        String token = jwtProvider.generateToken(user.getId(), user.getEmail());
        UserResponse userResponse = userMapper.mapEntityToResponse(user);

        return ResponseEntity.ok(new AuthResponse(token, userResponse, "Login successful"));
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}