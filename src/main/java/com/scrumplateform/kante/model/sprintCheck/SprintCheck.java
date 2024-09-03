package com.scrumplateform.kante.model.sprintCheck;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SprintCheck {
    private int status;
    private LocalDateTime checkDate; 
}
