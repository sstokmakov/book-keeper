package ru.tokmakov.bookkeeper.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleInvalidArgument(Exception e) {
        log.error("Handled BAD_REQUEST exception: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
