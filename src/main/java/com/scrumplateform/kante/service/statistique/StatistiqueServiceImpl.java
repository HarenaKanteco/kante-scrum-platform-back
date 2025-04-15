package com.scrumplateform.kante.service.statistique;

import java.util.List;
import java.util.Map;

import com.scrumplateform.kante.model.statistique.StatistiqueScrum;
import com.scrumplateform.kante.model.statistique.TacheRepartitionDTO;

public interface StatistiqueServiceImpl {
    public StatistiqueScrum getAllScrumStatistiques(String projetId);
    public Map<String, Object> getAnalyseTemporelle(String projetId);
    public Map<String, Object> getPerformanceStatistiques(String projetId);
    public Map<String, Object> getStatistiquesGenerales(String projetId);
    public Map<String, Object> getStatistiquesTaches(String projetId);
    public List<TacheRepartitionDTO> getRepartitionTaches(String projetId);
}
