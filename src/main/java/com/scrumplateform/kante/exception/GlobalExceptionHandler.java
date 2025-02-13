package com.scrumplateform.kante.exception;

import com.scrumplateform.kante.exception.calendrier.CalendrierNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CalendrierNotFoundException.class)
    public ResponseEntity<String> handleCalendrierNotFoundException(CalendrierNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
} 