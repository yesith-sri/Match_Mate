package com.edu.basic.user.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateProfileRequest {

    private String firstName;

    private String lastName;

    private Integer age;

    private String gender;

    private String phoneNumber;

    private String bio;

    private String city;

    private String country;

    private String profileImageUrl;

    // --- Matchmaking signals (optional) ---

    private Set<String> interests;

    private String seekingGender; // MALE / FEMALE / ANY

    private Integer minAgePref;

    private Integer maxAgePref;
}