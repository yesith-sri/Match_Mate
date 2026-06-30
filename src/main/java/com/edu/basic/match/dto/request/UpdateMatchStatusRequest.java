package com.edu.basic.match.dto.request;

import lombok.Data;

@Data
public class UpdateMatchStatusRequest {

    private String status; // SUGGESTED / CONFIRMED / REJECTED
}
