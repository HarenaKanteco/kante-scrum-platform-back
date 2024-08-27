package com.scrumplateform.kante.model.userStory;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "critereAcceptation")
public class CritereAcceptation {
    
    private String id;
    private String titre;
    private String details;
}

