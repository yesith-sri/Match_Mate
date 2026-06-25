package com.edu.basic.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── User ──────────────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    USER_DISABLED(HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST),

    // ── Auth / JWT ─────────────────────────────────────────
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),

    // ── Event ─────────────────────────────────────────────
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    EVENT_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST),
    EVENT_CAPACITY_EXCEEDED(HttpStatus.CONFLICT),
    EVENT_REGISTRATION_CLOSED(HttpStatus.BAD_REQUEST),
    EVENT_NOT_STARTED(HttpStatus.BAD_REQUEST),
    EVENT_ALREADY_ENDED(HttpStatus.BAD_REQUEST),

    // ── Booking ───────────────────────────────────────────
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND),
    BOOKING_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST),
    BOOKING_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST),
    BOOKING_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST),
    DUPLICATE_BOOKING(HttpStatus.CONFLICT),

    // ── Payment ───────────────────────────────────────────
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST),

    // ── Generic ───────────────────────────────────────────
    INVALID_REQUEST(HttpStatus.BAD_REQUEST),
    DUPLICATE_RECORD(HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    OPERATION_NOT_PERMITTED(HttpStatus.FORBIDDEN),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;
}