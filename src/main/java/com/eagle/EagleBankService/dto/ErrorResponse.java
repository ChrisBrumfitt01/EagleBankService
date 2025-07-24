package com.eagle.EagleBankService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;
}