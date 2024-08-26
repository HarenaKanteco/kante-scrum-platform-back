package com.scrumplateform.kante.model.entreprise;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "entreprise")
public class Entreprise {
    @Id
    private String id;
    private String nom;
}
