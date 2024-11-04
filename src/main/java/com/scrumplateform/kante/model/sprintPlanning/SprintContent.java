package com.scrumplateform.kante.model.sprintPlanning;

import java.util.Date;


import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.sprintCheck.SprintCheck;

import lombok.Data;

@Data
@Document(collection = "sprintContent")
public class SprintContent {
    private String id;
    private String titre;
    private String description;
    private int ordre;
    private Date dateDebut;
    private Date dateFin;
    private SprintCheck status;
    private int priorite = 1;
}

