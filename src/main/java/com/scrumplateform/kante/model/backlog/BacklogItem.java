package com.scrumplateform.kante.model.backlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BacklogItem {
    private String id;
    private String backlogId;
    private String titre;
    private String description;
    private String etat;
} 