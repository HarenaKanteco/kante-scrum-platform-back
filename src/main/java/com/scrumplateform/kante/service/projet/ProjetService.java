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

import com.scrumplateform.kante.exception.conception.ConceptionNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.exception.userStory.UserStoryNotFoundException;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.projet.ProjetProjection;
import com.scrumplateform.kante.model.sprintPlanning.Sprint;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.repository.projet.ProjetRepository;

@Service
public class ProjetService implements ProjetServiceImpl {

    @Autowired
    private ProjetRepository projetRepository;

    @Override
    public Projet updateSprintsInProject(String projetId, List<Sprint> updatedSprints) throws ProjectNotFoundException {
        Optional<Projet> optionalProjet = projetRepository.findById(projetId);
        
        if (optionalProjet.isEmpty()) {
            throw new ProjectNotFoundException("Projet non trouvé pour l'id : " + projetId);
        }

        Projet projet = optionalProjet.get();

        // Remplacer la liste existante des sprints par la nouvelle liste
        projet.setSprints(updatedSprints);

        // Sauvegarder le projet mis à jour dans la base de données
        return projetRepository.save(projet);
    }

    @Override
    public Projet updateConceptionInProject(String projetId, String conceptionId, Conception updatedConception) throws ProjectNotFoundException, ConceptionNotFoundException {
        // Récupérer le projet par ID
        Projet projet = getProjetById(projetId);

        if (projet == null) {
            throw new ProjectNotFoundException("Projet avec ID " + projetId + " non trouvé.");
        }

        // Récupérer la liste des conceptions du projet
        List<Conception> conceptions = projet.getConceptions();

        // Trouver la conception à mettre à jour
        Conception conceptionToUpdate = conceptions.stream()
            .filter(conception -> conception.getId().equals(conceptionId))
            .findFirst()
            .orElseThrow(() -> new ConceptionNotFoundException("Conception avec ID " + conceptionId + " non trouvée dans le projet."));

        // Mettre à jour les informations de la conception
        conceptionToUpdate.setTitre(updatedConception.getTitre());
        conceptionToUpdate.setDescription(updatedConception.getDescription());
        conceptionToUpdate.setContenu(updatedConception.getContenu());

        // Sauvegarder les modifications dans le projet
        projetRepository.save(projet);

        return projet;
    }

    @Override
    public Page<Conception> getPaginatedConceptions(String projetId, int page, int size) {
        // Récupérer le projet par ID
        Projet projet = getProjetById(projetId);

        // Vérifier si la liste des conceptions est vide ou nulle
        if (projet.getConceptions() == null || projet.getConceptions().isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        }

        // Créer un objet Pageable pour la pagination
        Pageable pageable = PageRequest.of(page, size);

        // Calculer les indices de début et de fin pour la sous-liste paginée des Conceptions
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), projet.getConceptions().size());

        // Récupérer la sous-liste paginée des Conceptions
        List<Conception> paginatedConceptions = projet.getConceptions().subList(start, end);

        // Retourner la page des Conceptions
        return new PageImpl<>(paginatedConceptions, pageable, projet.getConceptions().size());
    }

    @Override
    public Projet addConceptionToProject(String projetId, Conception conception) {
        Projet projet = getProjetById(projetId);

        conception.setId(UUID.randomUUID().toString());

        if (projet.getConceptions() == null) {
            projet.setConceptions(new ArrayList<>());
        }

        projet.getConceptions().add(conception);
        return projetRepository.save(projet);
    }

    @Override
    public Projet updateTechnique(String projetId, Technique newTechnique) {
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet non trouvé avec l'ID : " + projetId));
        
        // Mise à jour de l'attribut 'technique'
        projet.setTechnique(newTechnique);
        
        // Sauvegarder le projet mis à jour
        return projetRepository.save(projet);
    }

    @Override
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

    @Override
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

    @Override
    public Projet addUserStoryToProject(String projetId, UserStory userStory) {
        Projet projet = getProjetById(projetId);

        userStory.setId(UUID.randomUUID().toString());

        if (projet.getUserStories() == null) {
            projet.setUserStories(new ArrayList<>());
        }

        projet.getUserStories().add(userStory);
        return projetRepository.save(projet);
    }

    @Override
    public Projet getProjetById(String projetId) {
        return projetRepository.findById(projetId)
                .orElseThrow(() -> new ProjectNotFoundException("Projet avec ID " + projetId + " n'existe pas."));
        }

    @Override
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

    @Override
    public Page<ProjetProjection> getPaginatedProjects(String scrumId, String keyword, int page, int size) {
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
