// UnauthorizedException.java
package com.edu.basic.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}