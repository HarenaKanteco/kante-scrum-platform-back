package com.scrumplateform.kante.model.deploiement;

import java.util.Date;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "deploiement")
public class Deploiement {
    
    private String id;
    private TypeDeploiement type;
    private String lien;
    private Date date;
}

