package com.scrumplateform.kante.controller.utilisateur;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.security.Role;
import com.scrumplateform.kante.service.utilisateur.UtilisateurService;

@RestController
@RequestMapping("/api/v1/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // Méthode pour récupérer les utilisateurs avec le rôle "DEV" et un rôle supplémentaire
    @GetMapping("/developpeurs/{additionalRole}")
    public ResponseEntity<Response> getDevelopersWithSpecificRole(@PathVariable("additionalRole") String additionalRole) {
        Response response = new Response();
        try {
            // Parse the string to a Role enum
            Role additionalRoleEnum = Role.valueOf(additionalRole.toUpperCase());

            // Call the service method with the Role enum
            List<Utilisateur> developers = utilisateurService.getDevelopersWithSpecificRole(additionalRoleEnum);
            
            if (developers.isEmpty()) {
                response.error(null, "Aucun développeur trouvé avec le rôle supplémentaire spécifié : " + additionalRole);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.success(developers, "Liste des développeurs récupérée avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Handle case where role string is not a valid enum value
            response.error(null, "Erreur : Rôle invalide spécifié - " + additionalRole);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération des développeurs : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
