package com.scrumplateform.kante.service.statistique;

import java.util.Map;

import com.scrumplateform.kante.model.statistique.StatistiqueScrum;

public interface StatistiqueServiceImpl {
    public StatistiqueScrum getAllScrumStatistiques(String projetId);
    public Map<String, Object> getAnalyseTemporelle(String projetId);
    public Map<String, Object> getPerformanceStatistiques(String projetId);
    public Map<String, Object> getStatistiquesGenerales(String projetId);
    public Map<String, Object> getStatistiquesTaches(String projetId);
}
