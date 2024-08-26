package com.scrumplateform.kante.model.deploiement;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "deploiement")
public class Deploiement {
    @Id
    private String id;
    private TypeDeploiement type;
    private String lien;
    private Date date;
}

