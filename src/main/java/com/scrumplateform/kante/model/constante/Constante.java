package com.scrumplateform.kante.model.constante;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.technique.CountRole;
import com.scrumplateform.kante.model.technique.Technologie;

import lombok.Data;

@Data
@Document(collection = "constante")
public class Constante {
    Pagination pagination;
    EtapeConstante etape;
    DevRole devRole;
    int etapeInitiale;
    int etapeFinale;
    NotificationConstante notification;
    List<Technologie> technologies;
    List<CountRole> countRoles;
    Template template;
    Developpement developpement;
    Environnement environnement;
}
