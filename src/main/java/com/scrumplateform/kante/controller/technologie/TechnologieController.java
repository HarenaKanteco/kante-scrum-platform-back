package com.scrumplateform.kante.controller.technologie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scrumplateform.kante.model.technique.Technologie;
import com.scrumplateform.kante.service.technique.TechnologieService;
import com.scrumplateform.kante.http.response.Response;

import java.util.List;

@RestController
@RequestMapping("/api/v1/technologies")
public class TechnologieController {

    @Autowired
    private TechnologieService technologieService;

    @GetMapping
    public ResponseEntity<Response> getAllTechnologies() {
        Response response = new Response();
        try {
            List<Technologie> technologies = technologieService.getAllTechnologies();
            response.success(technologies, "Technologies récupérées avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération des technologies : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
