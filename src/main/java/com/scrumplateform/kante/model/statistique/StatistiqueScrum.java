package com.scrumplateform.kante.model.statistique;

import java.util.Map;

import lombok.Data;

@Data
public class StatistiqueScrum {
    Map<String, Object> statistiquesGenerales;
    Map<String, Object> statistiquesTaches;
    Map<String, Object> statistiquesPerformance;
    Map<String, Object> analyseTemporelle;
}
