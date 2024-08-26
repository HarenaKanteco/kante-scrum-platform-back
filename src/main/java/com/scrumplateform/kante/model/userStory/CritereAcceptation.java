package com.scrumplateform.kante.model.userStory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "critereAcceptation")
public class CritereAcceptation {
    @Id
    private String id;
    private String titre;
    private String details;
}

