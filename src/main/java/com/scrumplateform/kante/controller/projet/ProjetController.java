package com.scrumplateform.kante.controller.projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.service.projet.ProjetService;

@RestController
@RequestMapping("/api/v1/projets")
public class ProjetController {

    @Autowired
    private ProjetService projetService;

    @GetMapping("/{projetId}")
    public ResponseEntity<Response> getProjetById(@PathVariable("projetId") String projetId) {
        Response response = new Response();
        try {
            Projet projet = projetService.getProjetById(projetId);
            response.success(projet, "Projet récupéré avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération du projet : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Response> getPaginatedProjects(
            @RequestParam("scrumId") String scrumId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {

        Response response = new Response();
        try {
            Page<Projet> projects = projetService.getPaginatedProjects(scrumId, keyword, page, size);
            
            // Create a success response in French
            response.success(projects, "Projets récupérés avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exceptions and create an error response in French
            response.error(null, "Une erreur est survenue lors de la récupération des projets : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

