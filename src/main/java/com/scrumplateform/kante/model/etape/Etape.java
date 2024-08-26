package com.scrumplateform.kante.model.etape;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "etape")
public class Etape {
    @Id
    private String id;
    private String label;
    private int ordre;
}

