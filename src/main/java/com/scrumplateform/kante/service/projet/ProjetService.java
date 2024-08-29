package com.scrumplateform.kante.service.projet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.exception.userStory.UserStoryNotFoundException;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.repository.projet.ProjetRepository;

@Service
public class ProjetService {

    @Autowired
    private ProjetRepository projetRepository;

    public Projet updateTechnique(String projetId, Technique newTechnique) {
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet non trouvé avec l'ID : " + projetId));
        
        // Mise à jour de l'attribut 'technique'
        projet.setTechnique(newTechnique);
        
        // Sauvegarder le projet mis à jour
        return projetRepository.save(projet);
    }

    public Page<UserStory> getPaginatedUserStories(String projetId, int page, int size) {
        // Récupérer le projet par ID
        Projet projet = getProjetById(projetId);

        if (projet.getUserStories() == null || projet.getUserStories().isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        }

        // Créer un objet Pageable pour la pagination
        Pageable pageable = PageRequest.of(page, size);

        // Récupérer la sous-liste paginée des UserStories
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), projet.getUserStories().size());
        List<UserStory> paginatedUserStories = projet.getUserStories().subList(start, end);

        // Retourner la page des UserStories
        return new PageImpl<>(paginatedUserStories, pageable, projet.getUserStories().size());
    }

    public Projet updateUserStoryInProject(String projetId, String userStoryId, UserStory updatedUserStory) {
        Projet projet = getProjetById(projetId);
    
        if (projet.getUserStories() == null || projet.getUserStories().isEmpty()) {
            throw new UserStoryNotFoundException("Aucune user story trouvée dans le projet avec l'ID: " + projetId);
        }
    
        Optional<UserStory> userStoryOptional = projet.getUserStories().stream()
                .filter(us -> us.getId().equals(userStoryId))
                .findFirst();
    
        if (!userStoryOptional.isPresent()) {
            throw new UserStoryNotFoundException("User story avec l'ID " + userStoryId + " non trouvée dans le projet.");
        }
    
        UserStory userStoryToUpdate = userStoryOptional.get();
        userStoryToUpdate.setTitre(updatedUserStory.getTitre());
        userStoryToUpdate.setDescription(updatedUserStory.getDescription());
        userStoryToUpdate.setCritereAcceptations(updatedUserStory.getCritereAcceptations());
        userStoryToUpdate.setScenarios(updatedUserStory.getScenarios());
        userStoryToUpdate.setDateCreation(updatedUserStory.getDateCreation());
    
        return projetRepository.save(projet);
    }    

    public Projet addUserStoryToProject(String projetId, UserStory userStory) {
        Projet projet = getProjetById(projetId);

        userStory.setId(UUID.randomUUID().toString());

        if (projet.getUserStories() == null) {
            projet.setUserStories(new ArrayList<>());
        }

        projet.getUserStories().add(userStory);
        return projetRepository.save(projet);
    }

    public Projet getProjetById(String projetId) {
        return projetRepository.findById(projetId)
                .orElseThrow(() -> new ProjectNotFoundException("Projet avec ID " + projetId + " n'existe pas."));
        }

    public Page<Projet> getPaginatedProjects(String scrumId, String keyword, int etapeOrdre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));

        // Constructing search criteria
        if (keyword == null || keyword.trim().isEmpty()) {
            return projetRepository.findByScrumIdAndEtapeOrdre(scrumId, etapeOrdre, pageable);
        } else {
            String regex = ".*" + keyword + ".*";
            return projetRepository.findByScrumIdAndKeywordAndEtapeOrdre(scrumId, regex, etapeOrdre, pageable);
        }
    }

    public Page<Projet> getPaginatedProjects(String scrumId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));

        // Constructing search criteria
        if (keyword == null || keyword.trim().isEmpty()) {
            return projetRepository.findByScrumId(scrumId, pageable);
        } else {
            String regex = ".*" + keyword + ".*";
            return projetRepository.findByScrumIdAndKeyword(scrumId, regex, pageable);
        }
    }
}
