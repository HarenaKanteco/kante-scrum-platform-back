package com.scrumplateform.kante.model.statistique;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TacheRepartitionDTO {
    private String email;
    private Long nombreTaches;
    private Double pourcentage;
} 