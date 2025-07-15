package com.auth.exception;

public class MalformedParsedString extends Exception {
    public MalformedParsedString(String message) {
        super(message);
    }

    public MalformedParsedString(String message, Throwable cause) {
        super(message, cause);
    }
}
