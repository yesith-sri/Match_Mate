// BusinessException.java
package com.edu.basic.exception;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}