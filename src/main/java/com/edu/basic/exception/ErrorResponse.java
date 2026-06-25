package com.edu.basic.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String errorCode;
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, String> validationErrors; // only populated on 400 validation
}