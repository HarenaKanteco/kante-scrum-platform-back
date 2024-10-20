package com.scrumplateform.kante.model.userStory;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "backlog")
public class Backlog {
    private String id;
    private String projetId;
    private List<Object> backlogItems;
    private List<String> commentaires;
    private int etat;
}
