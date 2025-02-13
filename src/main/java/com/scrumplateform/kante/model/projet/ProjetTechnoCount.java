package com.scrumplateform.kante.model.projet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjetTechnoCount {
    private String technologie;
    private long count;
}
