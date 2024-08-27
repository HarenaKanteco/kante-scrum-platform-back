package com.scrumplateform.kante.model.technique;

import java.util.List;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "technique")
public class Technique {
    
    private String id;
    private List<Technologie> technologies;
    private List<String> materiels;
    private List<String> envDev;
    private String commentaire;
}

