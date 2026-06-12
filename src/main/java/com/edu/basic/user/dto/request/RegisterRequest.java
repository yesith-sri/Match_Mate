package com.edu.basic.user.dto.request;

import lombok.Data;

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
}
