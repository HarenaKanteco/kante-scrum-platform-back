package com.scrumplateform.kante.exception.utilisateur;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}