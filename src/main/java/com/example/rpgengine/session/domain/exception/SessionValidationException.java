package com.example.rpgengine.session.domain.exception;

public class SessionValidationException extends RuntimeException{
    public SessionValidationException(String message) {
        super(message);
    }
}
