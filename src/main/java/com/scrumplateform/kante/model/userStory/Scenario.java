package com.scrumplateform.kante.model.userStory;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "scenario")
public class Scenario {
    
    private String id;
    private String titre;
    private String details;
}

