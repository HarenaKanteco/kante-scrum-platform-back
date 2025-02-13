package com.scrumplateform.kante.model.backlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "backlog")
public class Backlog {
    @Id
    private String id;
    private String projetId;
    private List<BacklogItem> backlogItems;
    private List<Commentaire> commentaires;
    private int etat;
} 