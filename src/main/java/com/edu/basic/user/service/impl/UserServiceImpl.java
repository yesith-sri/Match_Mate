package com.edu.basic.user.service.impl;

import com.edu.basic.user.mapper.UserMapper;
import com.edu.basic.user.dto.request.UpdateProfileRequest;
import com.edu.basic.user.dto.response.UserResponse;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.repositary.UserRepository;
import com.edu.basic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserResponse> getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserResponse response = userMapper.mapEntityToResponse(user);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.mapRequestToEntity(request, user);
        User updatedUser = userRepository.save(user);

        UserResponse response = userMapper.mapEntityToResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return userMapper.mapEntityToResponse(user);
    }
}