package com.scrumplateform.kante.model.developpement;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SprintDev {
    
    private String id;
    private String titre;
    private List<SprintContentDev> sprintContentDevs;
    private Date dateCreation;
    private Date dateDebut;
    private Date dateFin;
}

