package com.scrumplateform.kante.controller;

import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.service.BurndownChartService;
import com.scrumplateform.kante.service.projet.ProjetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/burndown")
public class BurndownChartController {

    private final BurndownChartService burndownChartService;
    private final ProjetService projetService;
    
    @Autowired
    public BurndownChartController(BurndownChartService burndownChartService, ProjetService projetService) {
        this.burndownChartService = burndownChartService;
        this.projetService = projetService;
    }
    
    @GetMapping("/{projetId}")
    public ResponseEntity<Map<String, Object>> getBurndownChartData(@PathVariable String projetId) {
        try {
            // Utiliser getProjetById au lieu de findById
            Projet projet = projetService.getProjetById(projetId);
            
            Map<LocalDate, Integer> burndownData = burndownChartService.generateBurndownChartData(projet);
            
            // Préparer la réponse
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("projetId", projetId);
            response.put("projetTitre", projet.getTitre());
            response.put("burndownData", formatBurndownData(burndownData));
            
            return ResponseEntity.ok(response);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/debug/{projetId}")
    public ResponseEntity<Map<String, Object>> getBurndownDebugData(@PathVariable String projetId) {
        try {
            Projet projet = projetService.getProjetById(projetId);
            
            Map<String, Object> debugInfo = new LinkedHashMap<>();
            debugInfo.put("projetId", projetId);
            debugInfo.put("projetTitre", projet.getTitre());
            debugInfo.put("dateLivraisonPrevue", projet.getDateLivraisonPrevue());
            
            // Collecter les infos sur les sprint devs
            List<Map<String, Object>> sprintDevsInfo = new ArrayList<>();
            
            if (projet.getSprintDevs() != null) {
                for (SprintDev sprintDev : projet.getSprintDevs()) {
                    Map<String, Object> sprintInfo = new LinkedHashMap<>();
                    sprintInfo.put("id", sprintDev.getId());
                    sprintInfo.put("titre", sprintDev.getTitre());
                    sprintInfo.put("dateDebut", sprintDev.getDateDebut());
                    sprintInfo.put("dateFin", sprintDev.getDateFin());
                    
                    // Compter les tâches terminées
                    long tasksDone = 0;
                    long totalTasks = 0;
                    
                    if (sprintDev.getSprintContentDevs() != null) {
                        totalTasks = sprintDev.getSprintContentDevs().size();
                        tasksDone = sprintDev.getSprintContentDevs().stream()
                            .filter(task -> task.getStatus() != null && task.getStatus().getStatus() == 10)
                            .count();
                        
                        // Collecter les infos sur les tâches terminées
                        List<Map<String, Object>> finishedTasks = sprintDev.getSprintContentDevs().stream()
                            .filter(task -> task.getStatus() != null && task.getStatus().getStatus() == 10)
                            .map(task -> {
                                Map<String, Object> taskInfo = new LinkedHashMap<>();
                                taskInfo.put("id", task.getId());
                                taskInfo.put("titre", task.getTitre());
                                taskInfo.put("priorite", task.getPriorite());
                                taskInfo.put("checkDate", task.getStatus().getCheckDate());
                                return taskInfo;
                            })
                            .collect(Collectors.toList());
                        
                        sprintInfo.put("finishedTasks", finishedTasks);
                    }
                    
                    sprintInfo.put("totalTasks", totalTasks);
                    sprintInfo.put("tasksDone", tasksDone);
                    
                    sprintDevsInfo.add(sprintInfo);
                }
            }
            
            debugInfo.put("sprintDevs", sprintDevsInfo);
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new LinkedHashMap<>();
            errorInfo.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorInfo);
        }
    }
    
    /**
     * Formate les données du burndown chart pour la réponse JSON
     */
    private Map<String, Integer> formatBurndownData(Map<LocalDate, Integer> burndownData) {
        Map<String, Integer> formattedData = new LinkedHashMap<>();
        
        burndownData.forEach((date, points) -> 
            formattedData.put(date.toString(), points)
        );
        
        return formattedData;
    }
} 