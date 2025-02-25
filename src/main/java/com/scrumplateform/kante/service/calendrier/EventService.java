package com.scrumplateform.kante.service.calendrier;

import com.scrumplateform.kante.model.calendrier.Evenement;;
import com.scrumplateform.kante.repository.calendrier.EventRepository;
import com.scrumplateform.kante.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Async
    protected void sendEventNotifications(Evenement event) {
        log.info("Tentative d'envoi des notifications pour l'événement : {}", event);
        
        if (event.getParticipants() == null || event.getParticipants().isEmpty()) {
            log.warn("Aucun participant trouvé pour l'événement : {} (ID: {})", event.getTitle(), event.getId());
            return;
        }

        Context context = new Context();
        context.setVariable("event", event);
        
        log.info("Envoi des notifications à {} participants", event.getParticipants().size());
        
        event.getParticipants().forEach(participantEmail -> {
            try {
                log.debug("Envoi de l'email à : {}", participantEmail);
                emailService.sendEmail(
                    participantEmail,
                    "Nouvel événement : " + event.getTitle(),
                    "event-notification",
                    context
                );
                log.debug("Email envoyé avec succès à : {}", participantEmail);
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email à {} : {}", participantEmail, e.getMessage());
            }
        });
    }

    public List<Evenement> getAllEvents() {
        return eventRepository.findAll();
    }

    public Evenement createEvent(Evenement event) {
        log.info("Création d'un nouvel événement : {}", event);
        
        // Initialiser la liste des participants si elle est nulle
        if (event.getParticipants() == null) {
            log.debug("Initialisation de la liste des participants");
            event.setParticipants(new ArrayList<>());
        }
        
        try {
            Evenement savedEvent = eventRepository.save(event);
            log.info("Événement sauvegardé avec succès : {}", savedEvent);
            sendEventNotifications(savedEvent);
            return savedEvent;
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde de l'événement : {}", e.getMessage());
            throw e;
        }
    }

    public Evenement getEventById(String id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
    }

    public Evenement updateEvent(String id, Evenement eventDetails) {
        Evenement event = getEventById(id);
        event.setTitle(eventDetails.getTitle());
        event.setStart(eventDetails.getStart());
        event.setEnd(eventDetails.getEnd());
        event.setDescription(eventDetails.getDescription());
        event.setParticipants(eventDetails.getParticipants());
        event.setType(eventDetails.getType());
        event.setPriority(eventDetails.getPriority());
        return eventRepository.save(event);
    }

    public void deleteEvent(String id) {
        Evenement event = getEventById(id);
        eventRepository.delete(event);
    }
} 