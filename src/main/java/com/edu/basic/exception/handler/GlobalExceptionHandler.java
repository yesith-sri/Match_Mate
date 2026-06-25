package com.edu.basic.exception.handler;

import com.edu.basic.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // ── i18n-based exceptions ──────────────────────────────
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("CommonException: errorCode={}, messageKey={}", ex.getErrorCode(), ex.getMessageKey(), ex);

        String message = messageSource.getMessage(
                ex.getMessageKey(), ex.getArgs(), Locale.getDefault());

        return build(ex.getErrorCode(), message, null);
    }

    // ── Domain exceptions (all extend BaseException) ───────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized: {}", ex.getMessage());
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("Business error: {}", ex.getMessage());
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(BookingLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleBookingLimitExceeded(BookingLimitExceededException ex) {
        log.error("Booking limit exceeded: {}", ex.getMessage());
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    // ── Validation ─────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_REQUEST.name())
                .status(ErrorCode.INVALID_REQUEST.getHttpStatus().value())
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .validationErrors(fieldErrors)
                .build();

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(response);
    }

    // ── Fallback ───────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return build(ErrorCode.INVALID_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error at {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return build(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred", null);
    }

    // ── Builder helper ─────────────────────────────────────
    private ResponseEntity<ErrorResponse> build(ErrorCode errorCode, String message, Map<String, String> validationErrors) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode.name())
                .status(errorCode.getHttpStatus().value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}