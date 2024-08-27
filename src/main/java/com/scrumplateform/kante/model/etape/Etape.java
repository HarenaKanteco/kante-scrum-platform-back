package com.scrumplateform.kante.model.etape;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "etape")
public class Etape {
    
    private String id;
    private String label;
    private String icon;
    private int ordre;
}

