package com.scrumplateform.kante.exception;

public class BacklogNotFoundException extends RuntimeException {
    public BacklogNotFoundException(String message) {
        super(message);
    }
} 