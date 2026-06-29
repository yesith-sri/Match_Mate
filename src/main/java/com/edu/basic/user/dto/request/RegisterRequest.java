package com.edu.basic.user.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private Integer age;

    private String gender;

    private String phoneNumber;

    private String city;

    private String country;

    // --- Matchmaking signals (optional) ---

    private Set<String> interests;

    private String seekingGender; // MALE / FEMALE / ANY

    private Integer minAgePref;

    private Integer maxAgePref;
}
