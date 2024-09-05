package com.scrumplateform.kante.model.developpement;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.utilisateur.Utilisateur;

import lombok.Data;

@Data
@Document(collection = "sprintContentDev")
public class SprintContentDev {
    private String id;
    private String titre;
    private String description;
    private Utilisateur responsable; 
    private Date dateDebut;
    private Date dateFin;
    private int ordre;
    private int status;
}
