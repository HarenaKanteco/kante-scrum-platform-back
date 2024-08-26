package com.scrumplateform.kante.model.meeting;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.utilisateur.Utilisateur;

import lombok.Data;

@Data
@Document(collection = "meeting")
public class Meeting {
    @Id
    private String id;
    private String date;
    private Utilisateur initiateur;
    private TypeMeeting typeMeeting;
    private int state;
}

