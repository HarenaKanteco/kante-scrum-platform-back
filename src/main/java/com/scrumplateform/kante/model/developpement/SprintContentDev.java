package com.scrumplateform.kante.model.developpement;

import java.util.Date;

import com.scrumplateform.kante.model.sprintCheck.SprintCheck;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;

import lombok.Data;

@Data
public class SprintContentDev {
    private String id;
    private String titre;
    private String description;
    private Utilisateur responsable; 
    private Date dateDebut;
    private Date dateFin;
    private int ordre;
    private SprintCheck status;
}
