package com.scrumplateform.kante.model.deploiement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "typeDeploiement")
public class TypeDeploiement {
    @Id
    private String id;
    private String label;
}

