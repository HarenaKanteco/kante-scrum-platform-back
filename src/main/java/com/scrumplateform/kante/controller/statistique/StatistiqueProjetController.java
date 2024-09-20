package com.scrumplateform.kante.controller.statistique;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.statistique.StatistiqueScrum;
import com.scrumplateform.kante.service.statistique.StatistiqueServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats/projets")
public class StatistiqueProjetController {

    @Autowired
    private StatistiqueServiceImpl statistiqueService;

    @GetMapping("/{projetId}/scrums")
    public ResponseEntity<Response> getAllStatistiques(@PathVariable("projetId") String projetId) {
        Response response = new Response();
        try {
            StatistiqueScrum allStats = statistiqueService.getAllScrumStatistiques(projetId);
            response.success(allStats, "Toutes les statistiques scrum master récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, "Une erreur est survenue lors de la récupération des statistiques : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projetId}/analyse-temporelles")
    public ResponseEntity<Response> getAnalyseTemporelle(@PathVariable("projetId") String projetId) {
        Response response = new Response();
        try {
            Map<String, Object> stats = statistiqueService.getAnalyseTemporelle(projetId);
            response.success(stats, "Analyse temporelle récupérée avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération de l'analyse temporelle : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projetId}/performances")
    public ResponseEntity<Response> getPerformanceStats(@PathVariable String projetId) {
        Response response = new Response();
        try {
            Map<String, Object> stats = statistiqueService.getPerformanceStatistiques(projetId);
            response.success(stats, "Statistiques de performance récupérées avec succès.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, "Erreur lors de la récupération des statistiques de performance.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}/taches")
    public ResponseEntity<Response> getStatistiquesTaches(@PathVariable String id) {
        Response response = new Response();
        try {
            Map<String, Object> stats = statistiqueService.getStatistiquesTaches(id);
            response.success(stats, "Statistiques des tâches récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, "Erreur lors de la récupération des statistiques des tâches : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/generales")
    public ResponseEntity<Response> getStatistiquesGenerales(@PathVariable String id) {
        Response response = new Response();
        try {
            Map<String, Object> stats = statistiqueService.getStatistiquesGenerales(id);
            response.success(stats, "Statistiques générales récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, "Erreur lors de la récupération des statistiques : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
