package com.scrumplateform.kante.model.cdcTechnique;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "cdcTechnique")
public class CdcTechnique {
    private String id;
    private String contenu;
}
