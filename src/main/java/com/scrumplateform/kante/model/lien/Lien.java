package com.scrumplateform.kante.model.lien;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "lien")
public class Lien {
    @Id
    private String id;
    private String label;
    private String valeur;
}
