package com.eagle.EagleBankService.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg){
        super(msg);
    }
}
