package com.edu.basic.exception.handler;

import com.edu.basic.exception.ErrorCode;
import com.edu.basic.exception.ErrorResponse;
import com.edu.basic.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Handle custom CommonException
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Object> handleCommonException(CommonException exception) {
        log.error("Handled CommonException: errorCode={}, messageKey={}",
                exception.getErrorCode(), exception.getMessageKey(), exception);

        String sourceMessage = messageSource.getMessage(
                exception.getMessageKey(), exception.getArgs(), Locale.getDefault());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getErrorCode(), sourceMessage, LocalDateTime.now()));
    }

    /**
     * Handle ALL unhandled / unknown exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(Exception exception) {
        log.error("Unhandled exception occurred:", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.INTERNAL_ERROR,
                        "An unexpected error occurred", LocalDateTime.now()));
    }
}
