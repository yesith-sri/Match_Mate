// CommonException.java  ← keep for message-key based i18n flow
package com.edu.basic.exception;

import lombok.Getter;

@Getter
public class CommonException extends BaseException {

    private final String messageKey;
    private final Object[] args;

    public CommonException(ErrorCode errorCode, String messageKey, Object... args) {
        super(errorCode, errorCode + " | " + messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}