package com.eagle.EagleBankService.exception;

import com.eagle.EagleBankService.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();

        ex.getBindingResult().getAllErrors().forEach(e -> {
            errorMessage.append(e.getDefaultMessage()).append("; ");
        });

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage.toString().trim());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
