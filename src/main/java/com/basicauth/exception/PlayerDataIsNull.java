package com.basicauth.exception;

public class PlayerDataIsNull extends Exception {
    public PlayerDataIsNull(String message) {
        super(message);
    }

    public PlayerDataIsNull(String message, Throwable cause) {
        super(message, cause);
    }
}
