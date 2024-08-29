package com.scrumplateform.kante.model.technique;


import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "technologie")
public class Technologie {
    private String id;
    private TechnologieType type;
    private String label;
}
