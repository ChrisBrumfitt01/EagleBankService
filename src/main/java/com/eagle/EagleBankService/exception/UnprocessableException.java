package com.eagle.EagleBankService.exception;

public class UnprocessableException extends RuntimeException {
    public UnprocessableException(String msg){
        super(msg);
    }
}
