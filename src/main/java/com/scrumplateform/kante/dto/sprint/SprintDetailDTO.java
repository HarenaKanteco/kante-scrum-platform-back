package com.scrumplateform.kante.dto.sprint;

import java.util.Date;
import java.util.List;

import com.scrumplateform.kante.model.meeting.Meeting;
import com.scrumplateform.kante.model.sprintPlanning.SprintContent;

import lombok.Data;

@Data
public class SprintDetailDTO {
    private String id;
    private String titre;
    private String description;
    private List<SprintContent> sprintContents;
    private List<Meeting> meetings;
    private Date dateDebut;
    private Date dateFin;
    private Date dateCreation;
    private Date dateMeeting;
    private int totalUserStories;
    private double completionPercentage;
} 