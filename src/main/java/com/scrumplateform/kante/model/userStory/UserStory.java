package com.scrumplateform.kante.model.userStory;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "userStory")
public class UserStory {
    @Id
    private String id;
    private String titre;
    private String description;
    private List<CritereAcceptation> critereAcceptations;
    private List<Scenario> scenarios;
    private String projetId;
    private Date dateCreation;
}
