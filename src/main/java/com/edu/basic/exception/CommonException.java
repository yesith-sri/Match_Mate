package com.edu.basic.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String messageKey;
    private final Object[] args;

    public CommonException(ErrorCode errorCode, String messageKey, Object... args) {
        super(errorCode + " | " + messageKey);
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.args = args;
    }
}
