package com.eagle.EagleBankService.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String msg){
        super(msg);
    }
}
