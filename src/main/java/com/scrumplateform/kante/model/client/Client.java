package com.scrumplateform.kante.model.client;


import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.model.entreprise.Entreprise;

import lombok.Data;

@Data
@Document(collection = "client")
public class Client {
    
    private String id;
    private String email;
    private Entreprise entreprise;
    private String logo;
    private String numero;
}
