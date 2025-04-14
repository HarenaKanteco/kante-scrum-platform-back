package com.scrumplateform.kante.service.projet;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import com.scrumplateform.kante.model.developpement.SprintContentDev;
import com.scrumplateform.kante.model.lien.Lien;
import com.scrumplateform.kante.model.projet.ProjetTechnoCount;
import com.scrumplateform.kante.model.sprintCheck.SprintCheck;
import com.scrumplateform.kante.model.sprintCheck.SprintDevCheckPercentage;
import com.scrumplateform.kante.model.technique.Technologie;
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
import com.scrumplateform.kante.dto.sprint.SprintDetailDTO;
import com.scrumplateform.kante.service.email.EmailService;
import org.thymeleaf.context.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import com.itextpdf.html2pdf.HtmlConverter;

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

    @Autowired
    private EmailService emailService;

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
        
        // Récupérer les anciens sprints pour comparaison
        List<SprintDev> oldSprintDevs = projet.getSprintDevs();
        
        // Map pour stocker les tâches par utilisateur
        Map<String, Map<String, List<SprintContentDev>>> userTasksMap = new HashMap<>();
        
        // Parcourir les sprints et collecter les tâches par utilisateur
        for (SprintDev sprintDev : updatedSprintDevs) {
            if (sprintDev.getSprintContentDevs() != null) {
                for (SprintContentDev content : sprintDev.getSprintContentDevs()) {
                    // Vérifier si le responsable existe avant de traiter la notification
                    if (content.getResponsable() != null && content.getResponsable().getId() != null) {
                        String userId = content.getResponsable().getId();
                        
                        // Vérifier si c'est une nouvelle tâche ou si le responsable a changé
                        boolean shouldNotify = isNewOrModifiedTask(oldSprintDevs, sprintDev.getId(), content);
                        
                        if (shouldNotify) {
                            // Initialiser la map pour l'utilisateur si nécessaire
                            userTasksMap.putIfAbsent(userId, new HashMap<>());
                            
                            // Initialiser la liste des tâches pour ce sprint si nécessaire
                            userTasksMap.get(userId).putIfAbsent(sprintDev.getTitre(), new ArrayList<>());
                            
                            // Ajouter la tâche à la liste
                            userTasksMap.get(userId).get(sprintDev.getTitre()).add(content);
                        }
                    }
                }
            }
        }
        
        // Envoyer un email par utilisateur avec toutes ses tâches
        for (Map.Entry<String, Map<String, List<SprintContentDev>>> userEntry : userTasksMap.entrySet()) {
            try {
                // Récupérer l'utilisateur
                Utilisateur responsable = utilisateurRepository.findById(userEntry.getKey())
                    .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
                
                // Préparer le contexte pour le template
                Context context = new Context();
                context.setVariable("responsableName", responsable.getEmail());
                context.setVariable("sprintTasksMap", userEntry.getValue());
                
                // Envoyer l'email
                emailService.sendEmail(
                    responsable.getEmail(),
                    "Assignation de tâches - Sprint Dev - Kante Scrum",
                    "sprint-dev-notification",
                    context
                );
            } catch (Exception e) {
                // Logger l'erreur mais continuer le processus
                e.printStackTrace();
            }
        }
        
        // Mettre à jour les sprints
        projet.setSprintDevs(updatedSprintDevs);
        
        // Sauvegarder le projet mis à jour
        return projetRepository.save(projet);
    }

    @Override
    public Projet updateSprintDevInDevTask(String projetId, List<SprintDev> updatedSprintDevs) throws ProjectNotFoundException {
        Optional<Projet> optionalProjet = projetRepository.findById(projetId);

        if (optionalProjet.isEmpty()) {
            throw new ProjectNotFoundException("Projet non trouvé pour l'id : " + projetId);
        }

        Projet projet = optionalProjet.get();
        
        // Récupérer les anciens sprints pour comparaison
        List<SprintDev> oldSprintDevs = projet.getSprintDevs();
        
        // Map pour stocker les tâches par utilisateur
        Map<String, Map<String, List<SprintContentDev>>> userTasksMap = new HashMap<>();
        
        // Parcourir les sprints et collecter les tâches par utilisateur
        for (SprintDev sprintDev : updatedSprintDevs) {
            if (sprintDev.getSprintContentDevs() != null) {
                for (SprintContentDev content : sprintDev.getSprintContentDevs()) {
                    if (content.getResponsable() != null) {
                        String userId = content.getResponsable().getId();
                        
                        // Vérifier si c'est une nouvelle tâche ou si le responsable a changé
                        boolean shouldNotify = isNewOrModifiedTask(oldSprintDevs, sprintDev.getId(), content);
                        
                        if (shouldNotify) {
                            // Initialiser la map pour l'utilisateur si nécessaire
                            userTasksMap.putIfAbsent(userId, new HashMap<>());
                            
                            // Initialiser la liste des tâches pour ce sprint si nécessaire
                            userTasksMap.get(userId).putIfAbsent(sprintDev.getTitre(), new ArrayList<>());
                            
                            // Ajouter la tâche à la liste
                            userTasksMap.get(userId).get(sprintDev.getTitre()).add(content);
                        }
                    }
                }
            }
        }
        
        // Mettre à jour les sprints
        projet.setSprintDevs(updatedSprintDevs);
        
        // Sauvegarder le projet mis à jour
        return projetRepository.save(projet);
    }

    private boolean isNewOrModifiedTask(List<SprintDev> oldSprintDevs, String sprintId, SprintContentDev newContent) {
        if (oldSprintDevs == null) return true;
        
        for (SprintDev oldSprint : oldSprintDevs) {
            if (oldSprint.getId().equals(sprintId) && oldSprint.getSprintContentDevs() != null) {
                for (SprintContentDev oldContent : oldSprint.getSprintContentDevs()) {
                    if (oldContent.getId().equals(newContent.getId())) {
                        // Vérifier si les deux contenus ont des responsables avant de comparer
                        if (oldContent.getResponsable() == null || newContent.getResponsable() == null) {
                            return oldContent.getResponsable() != newContent.getResponsable();
                        }
                        // Vérifier si le responsable a changé
                        return !oldContent.getResponsable().getId().equals(newContent.getResponsable().getId());
                    }
                }
            }
        }
        // Si on n'a pas trouvé la tâche dans les anciens sprints, c'est une nouvelle tâche
        return true;
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
    
        if (userStoryOptional.isEmpty()) {
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

    @Override
    public Projet updateLiensInProject(String projetId, List<Lien> updatedLiens) throws ProjectNotFoundException {
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet non trouvé avec l'ID : " + projetId));

        // Mise à jour de l'attribut 'liens'
        projet.setLiens(updatedLiens);

        // Sauvegarder le projet mis à jour
        return projetRepository.save(projet);
    }

    @Override
    public List<ProjetTechnoCount> getMostUsedTechnologies(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();

        List<Projet> projets = projetRepository.findTechnologiesByDateRange(startDate, endDate);
        Map<String, Long> technoCountMap = new HashMap<>();

        for (Projet projet : projets) {
            Technique technique = projet.getTechnique();
            if (technique != null && technique.getTechnologies() != null) {
                for (Technologie technologie : technique.getTechnologies()) {
                    technoCountMap.put(technologie.getLabel(), technoCountMap.getOrDefault(technologie.getLabel(), 0L) + 1);
                }
            }
        }

        return technoCountMap.entrySet().stream()
                .map(entry -> new ProjetTechnoCount(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<SprintDetailDTO> getProjetSprints(String projetId, int page, int size) {
        Projet projet = getProjetById(projetId);
        
        if (projet.getSprints() == null || projet.getSprints().isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        }

        List<SprintDetailDTO> sprintDetails = projet.getSprints().stream()
            // Filtrer pour ne garder que les sprints ayant au moins un SprintContent avec status 0
            .filter(sprint -> sprint.getSprintContents() != null && 
                sprint.getSprintContents().stream()
                    .anyMatch(content -> content.getStatus() != null && 
                             content.getStatus().getStatus() == 0))
            .map(sprint -> {
                SprintDetailDTO dto = new SprintDetailDTO();
                dto.setId(sprint.getId());
                dto.setTitre(sprint.getTitre());
                dto.setDescription(sprint.getDescription());
                // Ne garder que les SprintContents avec status 0
                dto.setSprintContents(
                    sprint.getSprintContents().stream()
                        .filter(content -> content.getStatus() != null && 
                                content.getStatus().getStatus() == 0)
                        .collect(Collectors.toList())
                );
                dto.setMeetings(sprint.getMeetings());
                dto.setDateDebut(sprint.getDateDebut());
                dto.setDateFin(sprint.getDateFin());
                dto.setDateCreation(sprint.getDateCreation());
                dto.setDateMeeting(sprint.getDateMeeting());
                
                // Calcul des statistiques uniquement pour les SprintContents avec status 0
                dto.setTotalUserStories(dto.getSprintContents().size());
                dto.setCompletionPercentage(0.0);
                
                return dto;
            })
            .collect(Collectors.toList());

        // Pagination
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sprintDetails.size());
        List<SprintDetailDTO> pageContent = sprintDetails.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sprintDetails.size());
    }

    @Override
    public SprintDevCheckPercentage getPercentageOfCompletedTask(String projetId) {
        // Récupérer le projet
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet non trouvé avec l'ID : " + projetId));

        long totalTasks = 0;
        long completedTasks = 0;

        // Vérifier si le projet a des sprints de développement
        if (projet.getSprintDevs() != null) {
            for (SprintDev sprintDev : projet.getSprintDevs()) {
                if (sprintDev.getSprintContentDevs() != null) {
                    for (SprintContentDev content : sprintDev.getSprintContentDevs()) {
                        totalTasks++;
                        // Vérifier si la tâche est terminée (status = 10)
                        if (content.getStatus() != null && content.getStatus().getStatus() == 10) {
                            completedTasks++;
                        }
                    }
                }
            }
        }

        // Calculer le pourcentage
        double percentage = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0;

        return new SprintDevCheckPercentage(totalTasks, completedTasks, percentage);
    }

    @Override
    public byte[] exportTachesDevParMois(String userId, int month, int year) throws IOException {
        // Créer un nouveau classeur Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tâches");
            
            // Récupérer l'utilisateur
            Utilisateur developpeur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Développeur non trouvé"));

            // Définir la période
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1, 0, 0, 0);
            Date startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            Date endDate = calendar.getTime();

            // Récupérer tous les projets où le développeur est dans l'équipe
            List<Projet> projets = projetRepository.findByEq(userId);

            // Style pour le titre
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style pour les en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Créer l'en-tête du rapport avec le nom du responsable
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Rapport des tâches de " + 
                developpeur.getEmail().split("@")[0] +
                " - " + new SimpleDateFormat("MMMM yyyy", Locale.FRENCH).format(startDate));
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // Créer les en-têtes des colonnes
            Row headerRow = sheet.createRow(2);
            String[] headers = {"Projet", "Sprint", "Tâche", "Période","période d'achèvement", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 3;
            for (Projet projet : projets) {
                if (projet.getSprintDevs() != null) {
                    for (SprintDev sprint : projet.getSprintDevs()) {
                        if (sprint.getSprintContentDevs() != null) {
                            for (SprintContentDev tache : sprint.getSprintContentDevs()) {
                                // Vérifier si la tâche appartient au développeur et à la période
                                if (tache.getResponsable() != null && 
                                    tache.getResponsable().getId().equals(userId) &&
                                    isDateInPeriod(tache.getDateDebut(), startDate, endDate)) {
                                    
                                    Row row = sheet.createRow(rowNum++);
                                    
                                    // Projet
                                    row.createCell(0).setCellValue(projet.getTitre());
                                    
                                    // Sprint
                                    row.createCell(1).setCellValue(sprint.getTitre());
                                    
                                    // Tâche
                                    row.createCell(2).setCellValue(tache.getTitre());
                                    
                                    // Période
                                    String periode = formatDatePeriod(tache.getDateDebut(), tache.getDateFin());
                                    row.createCell(3).setCellValue(periode);

                                    // Période d'achèvement
                                    String periodeAchevement = calculatePeriodDifference(
                                        tache.getDateDebut(), 
                                        tache.getStatus() != null ? tache.getStatus().getCheckDate() : null
                                    );
                                    row.createCell(4).setCellValue(periodeAchevement);
                                    
                                    // Statut avec style
                                    Cell statusCell = row.createCell(5);
                                    statusCell.setCellValue(getStatusLabel(tache.getStatus()));
                                    applyStatusStyle(statusCell, tache.getStatus());
                                }
                            }
                        }
                    }
                }
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir le workbook en bytes
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDateInPeriod(Date date, Date startDate, Date endDate) {
        return date != null && !date.before(startDate) && date.before(endDate);
    }

    private String formatDatePeriod(Date dateDebut, Date dateFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dateDebut == null && dateFin == null) {
            return "Non définie";
        }
        return (dateDebut != null ? sdf.format(dateDebut) : "?") + 
               " - " + 
               (dateFin != null ? sdf.format(dateFin) : "?");
    }

    private String getStatusLabel(SprintCheck status) {
        if (status == null) return "En cours";
        
        switch (status.getStatus()) {
            case 0: return "En cours";
            case 10: return "Terminé";
            default: return "Statut inconnu";
        }
    }

    private void applyStatusStyle(Cell cell, SprintCheck status) {
        CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
        
        if (status == null || status.getStatus() == 0) {
            // Style pour "Pas commencé" - Fond bleu clair
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        } else if (status.getStatus() == 10) {
            // Style pour "Terminé" - Fond vert clair
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        }
        
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    private String calculatePeriodDifference(Date startDate, LocalDateTime checkDateTime) {
        if (startDate == null || checkDateTime == null) {
            return "N/A";
        }

        // Convertir Date en LocalDateTime
        LocalDateTime startDateTime = startDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        // Calculer la différence entre les LocalDateTime
        long diffInHours = ChronoUnit.HOURS.between(startDateTime, checkDateTime);
        long diffInDays = ChronoUnit.DAYS.between(startDateTime, checkDateTime);

        if (diffInDays > 0) {
            if (diffInDays == 1)
                return "1 jour";
            return diffInDays + " jours";
        } else {
            if (diffInHours <= 1)
                return "1 heure";
            return diffInHours + " heures";
        }
    }

    @Override
    public byte[] exportCdcTechniquePdf(String projetId) throws IOException {
        // Récupérer le projet
        Projet projet = getProjetById(projetId);
        if (projet == null || projet.getCdcTechnique() == null) {
            throw new ProjectNotFoundException("Projet ou CDC technique non trouvé");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Créer le template HTML avec le style
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            margin: 40px;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .logo {
                            max-width: 150px;
                            margin-bottom: 20px;
                        }
                        h1 {
                            color: #40D9D9;
                            margin-bottom: 20px;
                        }
                        .content {
                            margin-top: 20px;
                        }
                        .footer {
                            margin-top: 30px;
                            text-align: center;
                            font-size: 12px;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Cahier des Charges Technique</h1>
                        <h2>%s</h2>
                    </div>
                    <div class="content">
                        %s
                    </div>
                    <div class="footer">
                        <p>© %d KanteCo. Tous droits réservés.</p>
                    </div>
                </body>
                </html>
                """.formatted(
                    projet.getTitre(),
                    projet.getCdcTechnique().getContenu(),
                    java.time.Year.now().getValue()
                );

            // Convertir HTML en PDF
            HtmlConverter.convertToPdf(htmlContent, baos);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new IOException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}
