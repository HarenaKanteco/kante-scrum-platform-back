package com.scrumplateform.kante.dto.account.exception;

public class UserNotFoundException  extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}
