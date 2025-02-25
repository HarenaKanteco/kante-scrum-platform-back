package com.scrumplateform.kante.service.backlog;

import com.scrumplateform.kante.exception.BacklogNotFoundException;
import com.scrumplateform.kante.model.backlog.Backlog;
import com.scrumplateform.kante.model.backlog.BacklogItem;
import com.scrumplateform.kante.repository.backlog.BacklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BacklogService {

    private final BacklogRepository backlogRepository;

    @Autowired
    public BacklogService(BacklogRepository backlogRepository) {
        this.backlogRepository = backlogRepository;
    }

    public List<Backlog> findAll() {
        return backlogRepository.findAll();
    }

    public Backlog findByIdProject(String projetId) {
        return backlogRepository.findByProjetId(projetId)
                .orElseThrow(() -> new BacklogNotFoundException("Backlog not found for project ID: " + projetId));
    }

    public List<BacklogItem> findBacklogItemsByProjectIdAndEtat(String projetId, String etat) {
        Backlog backlog = backlogRepository.findByProjetId(projetId)
                .orElseThrow(() -> new BacklogNotFoundException("Backlog non trouvé pour le projet ID: " + projetId));
                
        return backlog.getBacklogItems().stream()
                .filter(item -> item.getEtat().equals(etat))
                .collect(Collectors.toList());
    }

    public BacklogItem updateBacklogItemEtat(String projetId, String backlogItemId, String nouvelEtat) {
        Backlog backlog = backlogRepository.findByProjetId(projetId)
                .orElseThrow(() -> new BacklogNotFoundException("Backlog non trouvé pour le projet ID: " + projetId));

        BacklogItem itemToUpdate = backlog.getBacklogItems().stream()
                .filter(item -> item.getId().equals(backlogItemId))
                .findFirst()
                .orElseThrow(() -> new BacklogNotFoundException("BacklogItem non trouvé avec l'ID: " + backlogItemId));

        itemToUpdate.setEtat(nouvelEtat);
        backlogRepository.save(backlog);
        
        return itemToUpdate;
    }
}
