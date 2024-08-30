package com.scrumplateform.kante.controller.projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scrumplateform.kante.exception.conception.ConceptionNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.exception.userStory.UserStoryNotFoundException;
import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.service.etape.EtapeService;
import com.scrumplateform.kante.service.projet.ProjetService;
import com.scrumplateform.kante.service.utilisateur.UtilisateurService;

@RestController
@RequestMapping("/api/v1/projets")
public class ProjetController {

    @Autowired
    private ProjetService projetService;

    @Autowired
    private EtapeService etapeService;

    @Autowired
    private UtilisateurService utilisateurService;

    @PutMapping("/{projetId}/conceptions/{conceptionId}")
    public ResponseEntity<Response> updateConceptionInProject(
            @PathVariable("projetId") String projetId,
            @PathVariable("conceptionId") String conceptionId,
            @RequestBody Conception updatedConception) {

        Response response = new Response();
        try {
            Projet updatedProjet = projetService.updateConceptionInProject(projetId, conceptionId, updatedConception);
            response.success(updatedProjet, "Conception modifiée avec succès dans le projet.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException | ConceptionNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la modification de la conception : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projetId}/conceptions")
    public ResponseEntity<Response> getPaginatedConceptions(
            @PathVariable("projetId") String projetId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Response response = new Response();
        try {
            Page<Conception> conceptions = projetService.getPaginatedConceptions(projetId, page, size);
            response.success(conceptions, "Conceptions récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération des conceptions : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{projetId}/conceptions")
    public ResponseEntity<Response> addConceptionToProject(
            @PathVariable("projetId") String projetId,
            @RequestBody Conception conception) {

        Response response = new Response();
        try {
            Projet updatedProjet = projetService.addConceptionToProject(projetId, conception);
            response.success(updatedProjet, "Conception ajoutée avec succès au projet.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de l'ajout de la conception : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{projetId}/technics")
    public ResponseEntity<Response> updateTechnique(
            @PathVariable("projetId") String projetId,
            @RequestBody Technique newTechnique) {

        Response response = new Response();
        try {
            Projet updatedProjet = projetService.updateTechnique(projetId, newTechnique);
            response.success(updatedProjet, "Technique du projet mise à jour avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            response.error(null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la mise à jour de la technique du projet : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{projetId}/valider-etape")
    public ResponseEntity<Response> validerEtape(
            @PathVariable("projetId") String projetId,
            @RequestParam("utilisateurId") String utilisateurId,
            @RequestParam("ordreActuel") int ordreActuel,
            @RequestParam("ordreSuivant") int ordreSuivant) {

        Response response = new Response();
        try {
            // Simuler la récupération de l'utilisateur connecté, remplacez par une vraie méthode selon votre contexte
            Utilisateur utilisateurConnecte = utilisateurService.getUtilisateurById(utilisateurId);
            
            Projet projet = etapeService.validerEtape(projetId, utilisateurConnecte, ordreActuel, ordreSuivant);
            
            // Créer une réponse de succès
            response.success(projet, "Étape validée avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Gérer les exceptions et créer une réponse d'erreur
            response.error(null, "Une erreur est survenue lors de la validation de l'étape : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projetId}/userStories")
    public ResponseEntity<Response> getPaginatedUserStories(
            @PathVariable("projetId") String projetId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {

        Response response = new Response();
        try {
            Page<UserStory> paginatedUserStories = projetService.getPaginatedUserStories(projetId, page, size);

            // Créer une réponse de succès
            response.success(paginatedUserStories, "User stories récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            // Gérer les exceptions et créer une réponse d'erreur
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération des user stories : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{projetId}/userStories/{userStoryId}")
    public ResponseEntity<Response> updateUserStoryInProject(
            @PathVariable("projetId") String projetId,
            @PathVariable("userStoryId") String userStoryId,
            @RequestBody UserStory updatedUserStory) {

        Response response = new Response();
        try {
            Projet updatedProjet = projetService.updateUserStoryInProject(projetId, userStoryId, updatedUserStory);
            response.success(updatedProjet, "User story mise à jour avec succès dans le projet.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException | UserStoryNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la mise à jour de la user story : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{projetId}/userStories")
    public ResponseEntity<Response> addUserStoryToProject(
            @PathVariable("projetId") String projetId,
            @RequestBody UserStory userStory) {

        Response response = new Response();
        try {
            Projet updatedProjet = projetService.addUserStoryToProject(projetId, userStory);
            response.success(updatedProjet, "User story ajoutée avec succès au projet.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de l'ajout de la user story : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


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

    @GetMapping("steps")
    public ResponseEntity<Response> getPaginatedProjectsWithStep(
            @RequestParam("scrumId") String scrumId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "step", defaultValue = "1") int etapeOrdre,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {

        Response response = new Response();
        try {
            Page<Projet> projects = projetService.getPaginatedProjects(scrumId, keyword, etapeOrdre, page, size);
            
            // Create a success response in French
            response.success(projects, "Projets récupérés avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle any exceptions and create an error response in French
            response.error(null, "Une erreur est survenue lors de la récupération des projets : " + e.getMessage());
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

