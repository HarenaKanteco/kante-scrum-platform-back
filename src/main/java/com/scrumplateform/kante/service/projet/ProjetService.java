package com.scrumplateform.kante.service.projet;

import java.util.ArrayList;
import java.util.Date;
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

import com.scrumplateform.kante.dto.account.exception.UserNotFoundException;
import com.scrumplateform.kante.dto.projet.CreateProjetDTO;
import com.scrumplateform.kante.exception.client.ClientNotFoundException;
import com.scrumplateform.kante.exception.conception.ConceptionNotFoundException;
import com.scrumplateform.kante.exception.etape.EtapeNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.exception.userStory.UserStoryNotFoundException;
import com.scrumplateform.kante.model.cdcTechnique.CdcTechnique;
import com.scrumplateform.kante.model.client.Client;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.constante.Constante;
import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.etape.Etape;
import com.scrumplateform.kante.model.etape.EtapeProjet;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.projet.ProjetProjection;
import com.scrumplateform.kante.model.sprintPlanning.Sprint;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.repository.client.ClientRepository;
import com.scrumplateform.kante.repository.etape.EtapeRepository;
import com.scrumplateform.kante.repository.projet.ProjetRepository;
import com.scrumplateform.kante.repository.utilisateur.UtilisateurRepository;
import com.scrumplateform.kante.security.Role;
import com.scrumplateform.kante.service.constante.ConstanteService;
import com.scrumplateform.kante.service.notification.NotificationServiceImpl;

@Service
public class ProjetService implements ProjetServiceImpl {

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private EtapeRepository etapeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired 
    private ConstanteService constanteService;

    @Autowired 
    private NotificationServiceImpl notificationService;

    @Override
    public void sendProjectAssignationNotification(String idUtilisateur) throws Exception {
        Constante constante = constanteService.getConstante();
        String contenu = constante.getNotification().getProjet().getAssignation();
        notificationService.createNotification(idUtilisateur, contenu);
    }   

    @Override
    public Projet creerProjet(CreateProjetDTO projetDTO) throws Exception {
        // 1. Récupérer le scrum (Utilisateur) et le client (Client) à partir de leurs IDs
        Utilisateur scrum = utilisateurRepository.findById(projetDTO.getScrumId())
                                .orElseThrow(() -> new UserNotFoundException("Scrum introuvable"));

        if (!scrum.getRoles().contains(Role.SCRUM)) {
            throw new Exception("L'utilisateur sélectionné n'a pas le rôle SCRUM");
        }

        Client client = clientRepository.findById(projetDTO.getClientId())
                                .orElseThrow(() -> new ClientNotFoundException("Client introuvable"));

        // 2. Créer le projet avec les données fournies
        Projet projet = new Projet();
        projet.setScrum(scrum);
        projet.setClient(client);
        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setLiens(projetDTO.getLiens());
        projet.setDateCreation(new Date());
        projet.setDateLivraisonPrevue(projetDTO.getDateLivraisonPrevue());

        initializeEtape(projet);

        // 5. Enregistrer le projet dans la base de données
        Projet projetSaved = projetRepository.save(projet);

        // 6. Envoi de la notification
        sendProjectAssignationNotification(scrum.getId());

        return projetSaved;
    }

    @Override
    public void initializeEtape(Projet projet) throws Exception {
        Constante constante = constanteService.getConstante();

        // 3. Trouver l'étape avec ordre = 1, utilisateur = null, et dateValidation = null
        Etape etapeInitiale = etapeRepository.findByOrdre(constante.getEtapeInitiale())
            .orElseThrow(() -> new EtapeNotFoundException("Étape initiale introuvable"));
        
        // 4. Enregistrer l'étape projet avec les détails
        EtapeProjet etapeProjet = new EtapeProjet();
        etapeProjet.setId(UUID.randomUUID().toString());
        etapeProjet.setEtape(etapeInitiale);
        projet.setEtape(etapeProjet);

        projet.setEtapes(new ArrayList<>());
        projet.getEtapes().add(etapeProjet);
    }

    @Override
    public List<ProjetProjection> getProjects(String scrumId, int etapeOrdre) {
        List<ProjetProjection> projets = projetRepository.findByScrumIdAndEtapeOrdre(scrumId, etapeOrdre, Sort.by(Sort.Direction.ASC, "dateCreation"));
        return projets;
    }
    
    @Override
    public Page<ProjetProjection> getProjetsParMembreEquipe(String utilisateurId, String keyword, int step, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));

        if (keyword == null || keyword.trim().isEmpty()) {
            return projetRepository.findByEquipeAndEtapeOrdre(utilisateurId, step, pageable);
        } else {
            String regex = ".*" + keyword + ".*";
            return projetRepository.findByEquipeAndKeywordAndEtapeOrdre(utilisateurId, regex, step, pageable);
        }
    }

    @Override
    public Projet updateSprintDevsInProject(String projetId, List<SprintDev> updatedSprintDevs) throws ProjectNotFoundException {
        Optional<Projet> optionalProjet = projetRepository.findById(projetId);
        
        if (optionalProjet.isEmpty()) {
            throw new ProjectNotFoundException("Projet non trouvé pour l'id : " + projetId);
        }

        Projet projet = optionalProjet.get();

        // Remplacer la liste existante des sprints par la nouvelle liste
        projet.setSprintDevs(updatedSprintDevs);

        // Sauvegarder le projet mis à jour dans la base de données
        return projetRepository.save(projet);
    }

    @Override
    public Projet updateCdcTechniqueInProject(String projetId, CdcTechnique updatedCdcTechnique) throws ProjectNotFoundException {
        // Find the projet by id
        Projet projet = projetRepository.findById(projetId)
        .orElseThrow(() -> new ProjectNotFoundException("Projet not found with id " + projetId));

        // Update the cdcTechnique attribute
        projet.setCdcTechnique(updatedCdcTechnique);

        // Save the updated projet back to the database
        return projetRepository.save(projet);
    }

    @Override
    public Projet updateEquipeInProject(String projetId, List<Utilisateur> updatedEquipe) throws ProjectNotFoundException {
        // Find the projet by id
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet not found with id " + projetId));

        // Update the equipe attribute
        projet.setEquipe(updatedEquipe);

        // Save the updated projet back to the database
        return projetRepository.save(projet);
    }

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
