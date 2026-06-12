package com.edu.basic.user.service;

import com.edu.basic.service.BasicService;
import com.edu.basic.user.dto.request.UpdateProfileRequest;
import com.edu.basic.user.dto.response.UserResponse;
import com.edu.basic.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService extends BasicService<UpdateProfileRequest, User, UserResponse> {

    ResponseEntity<UserResponse> getUserProfile(Long userId);

    ResponseEntity<UserResponse> updateUserProfile(Long userId, UpdateProfileRequest request);

    ResponseEntity<Void> deleteUser(Long userId);

    boolean userExists(Long userId);

    UserResponse getUserById(Long userId);
}