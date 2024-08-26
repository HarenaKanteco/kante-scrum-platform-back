package com.scrumplateform.kante.model.conception;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "conception")
public class Conception {
    @Id
    private String id;
    private String titre;
    private String description;
    private String contenu;
}
