package com.scrumplateform.kante.model.projet;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.cdcTechnique.CdcTechnique;
import com.scrumplateform.kante.model.client.Client;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.deploiement.Deploiement;
import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.etape.EtapeProjet;
import com.scrumplateform.kante.model.feedback.Feedback;
import com.scrumplateform.kante.model.lien.Lien;
import com.scrumplateform.kante.model.sprintPlanning.Sprint;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import lombok.Data;

@Data
@Document(collection = "projet")
public class Projet {
    @Id
    private String id;
    private Utilisateur scrum;
    private Client client;
    private String titre;
    private String description;
    private List<Lien> liens;
    private Date dateCreation;
    private List<Date> dateLivraisonPrevue;
    private Date dateLivraison;
    private Date dateCloture;
    private EtapeProjet etape;
    private List<EtapeProjet> etapes;
    private List<Utilisateur> equipe;
    private List<UserStory> userStories;
    private Technique technique;
    private List<Conception> conceptions;
    private List<Sprint> sprints;
    private CdcTechnique cdcTechnique;
    private List<SprintDev> sprintDevs;
    private List<Deploiement> deploiements;
    private Feedback feedback;
}

