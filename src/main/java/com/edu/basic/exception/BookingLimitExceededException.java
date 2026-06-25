// BookingLimitExceededException.java
package com.edu.basic.exception;

public class BookingLimitExceededException extends BaseException {
    public BookingLimitExceededException(String message) {
        super(ErrorCode.BOOKING_LIMIT_EXCEEDED, message);
    }
}