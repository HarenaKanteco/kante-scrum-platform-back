package com.scrumplateform.kante.service.calendrier;

import com.scrumplateform.kante.model.calendrier.Evenement;;
import com.scrumplateform.kante.repository.calendrier.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Evenement> getAllEvents() {
        return eventRepository.findAll();
    }

    public Evenement createEvent(Evenement event) {
        return eventRepository.save(event);
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