package com.edu.basic.user.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {

    private Long userId;

    private String email;

    private String firstName;

    private String lastName;

    private Integer age;

    private String gender;

    private String phoneNumber;

    private String bio;

    private String role;

    private Boolean isActive;

    private Boolean isEmailVerified;

    private String city;

    private String country;

    private String profileImageUrl;

    private Set<String> interests;

    private String seekingGender;

    private Integer minAgePref;

    private Integer maxAgePref;

    private String createdAt;

    private String updatedAt;
}