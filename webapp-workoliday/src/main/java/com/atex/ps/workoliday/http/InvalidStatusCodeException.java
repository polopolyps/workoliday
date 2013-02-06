package com.atex.ps.workoliday.http;

@SuppressWarnings("serial")
public class InvalidStatusCodeException extends RuntimeException {
    private int code;

    public InvalidStatusCodeException(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
