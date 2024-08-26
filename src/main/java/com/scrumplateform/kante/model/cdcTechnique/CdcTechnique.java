package com.scrumplateform.kante.model.cdcTechnique;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "cdcTechnique")
public class CdcTechnique {
    @Id
    private String id;
    private String contenu;
}
