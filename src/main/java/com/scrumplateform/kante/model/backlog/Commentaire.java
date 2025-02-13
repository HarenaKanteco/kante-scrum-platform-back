package com.scrumplateform.kante.model.backlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Commentaire {
    private String id;
    private Date creation;
    private Utilisateur utilisateur;
    private String contenu;
    private int type;
    private boolean isResolved;
    private int etat;
} 