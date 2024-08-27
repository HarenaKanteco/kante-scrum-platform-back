package com.scrumplateform.kante.model.entreprise;


import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "entreprise")
public class Entreprise {
    
    private String id;
    private String nom;
}
