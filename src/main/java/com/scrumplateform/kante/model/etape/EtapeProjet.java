package com.scrumplateform.kante.model.etape;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.utilisateur.Utilisateur;

import lombok.Data;

@Data
@Document(collection = "etapeProjet")
public class EtapeProjet {
    @Id
    private String id;
    private Etape etape;
    private String projetId;
    private Utilisateur utilisateur;
    private Date dateValidation;
}
