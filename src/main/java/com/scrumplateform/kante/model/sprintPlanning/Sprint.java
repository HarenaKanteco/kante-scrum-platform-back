package com.scrumplateform.kante.model.sprintPlanning;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.meeting.Meeting;

import lombok.Data;

@Data
@Document(collection = "sprint")
public class Sprint {
    @Id
    private String id;
    private String titre;
    private String description;
    private List<SprintContent> sprintContents;
    private List<Meeting> meetings;
    private Date dateDebut;
    private Date dateFin;
    private Date dateCreation;
    private Date dateMeeting;
}
