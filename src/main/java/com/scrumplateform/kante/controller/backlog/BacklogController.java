package com.scrumplateform.kante.controller.backlog;
import com.scrumplateform.kante.exception.BacklogNotFoundException;
import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.backlog.Backlog;
import com.scrumplateform.kante.model.backlog.BacklogItem;
import com.scrumplateform.kante.service.backlog.BacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backlogs")
public class BacklogController {

    private final BacklogService backlogService;

    @Autowired
    public BacklogController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @GetMapping
    public ResponseEntity<List<Backlog>> findAll() {
        List<Backlog> backlogs = backlogService.findAll();
        return new ResponseEntity<>(backlogs, HttpStatus.OK);
    }

    @GetMapping("/{projetId}")
    public ResponseEntity<Backlog> findByIdProject(@PathVariable String projetId) {
        try {
            Backlog backlog = backlogService.findByIdProject(projetId);
            return new ResponseEntity<>(backlog, HttpStatus.OK);
        } catch (BacklogNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{projetId}/items/etat/{etat}")
    public ResponseEntity<Response> findBacklogItemsByProjectIdAndEtat(
            @PathVariable String projetId,
            @PathVariable String etat) {
        Response response = new Response();
        try {
            List<BacklogItem> backlogItems = backlogService.findBacklogItemsByProjectIdAndEtat(projetId, etat);
            response.success(backlogItems, "Éléments du backlog récupérés avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BacklogNotFoundException e) {
            response.error(null, "Erreur : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}