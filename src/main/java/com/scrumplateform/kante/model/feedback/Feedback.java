package com.scrumplateform.kante.model.feedback;


import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "feedback")
public class Feedback {
    
    private String id;
    private int note; // Use int if it's an integer rating, otherwise double for fractional ratings
    private String commentaire;
}

