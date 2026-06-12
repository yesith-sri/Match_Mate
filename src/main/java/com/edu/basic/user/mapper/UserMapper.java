package com.edu.basic.user.mapper;

import com.edu.basic.user.dto.request.UpdateProfileRequest;
import com.edu.basic.user.dto.response.UserResponse;
import com.edu.basic.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class UserMapper {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserResponse mapEntityToResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBio(user.getBio());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setCity(user.getCity());
        response.setCountry(user.getCountry());
        response.setProfileImageUrl(user.getProfileImageUrl());

        if (user.getCreatedAt() != null) {
            response.setCreatedAt(user.getCreatedAt().format(formatter));
        }
        if (user.getUpdatedAt() != null) {
            response.setUpdatedAt(user.getUpdatedAt().format(formatter));
        }

        return response;
    }

    public void mapRequestToEntity(UpdateProfileRequest request, User user) {
        if (request == null) {
            return;
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
    }
}
