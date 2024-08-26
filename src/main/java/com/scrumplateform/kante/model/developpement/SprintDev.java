package com.scrumplateform.kante.model.developpement;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "sprintDev")
public class SprintDev {
    @Id
    private String id;
    private String titre;
    private List<SprintContentDev> sprintContentDevs;
    private Date dateCreation;
    private Date dateDebut;
    private Date dateFin;
}

