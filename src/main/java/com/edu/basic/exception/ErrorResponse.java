package com.edu.basic.exception;

import java.time.LocalDateTime;

public record ErrorResponse(ErrorCode errorCode, String message, LocalDateTime timestamp) {
}
