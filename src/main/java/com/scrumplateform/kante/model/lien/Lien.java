package com.scrumplateform.kante.model.lien;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.UUID;

@Data
@Document(collection = "lien")
public class Lien {
    
    private String id;
    private String label;
    private String valeur;

    public Lien() {
        this.id = UUID.randomUUID().toString();
    }

    public Lien(String label, String valeur) {
        this.id = UUID.randomUUID().toString(); // Générer un ID unique pour chaque lien
        this.label = label;
        this.valeur = valeur;
    }
}
