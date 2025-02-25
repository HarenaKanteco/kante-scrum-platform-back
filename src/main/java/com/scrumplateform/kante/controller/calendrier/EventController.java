package com.scrumplateform.kante.controller.calendrier;

import com.scrumplateform.kante.model.calendrier.Evenement;
import com.scrumplateform.kante.service.calendrier.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvent(@RequestBody(required = true) Evenement event) {
        log.info("Données reçues du front-end : {}", event);
        
        // Validation des données
        if (event == null) {
            log.error("Les données de l'événement sont nulles");
            return ResponseEntity.badRequest().build();
        }

        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            log.error("Le titre de l'événement est requis. Données reçues : {}", event);
            return ResponseEntity.badRequest().build();
        }
        
        if (event.getStart() == null || event.getEnd() == null) {
            log.error("Les dates de début et de fin sont requises. Données reçues : {}", event);
            return ResponseEntity.badRequest().build();
        }

        try {
            log.info("Tentative de création de l'événement avec les données : {}", event);
            Evenement savedEvent = eventService.createEvent(event);
            log.info("Événement créé avec succès : {}", savedEvent);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'événement : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvent(@PathVariable String id, @RequestBody Evenement eventDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
} 