package com.scrumplateform.kante.service.projet;

import java.io.IOException;
import java.util.List;

import com.scrumplateform.kante.model.projet.ProjetTechnoCount;
import com.scrumplateform.kante.model.sprintCheck.SprintDevCheckPercentage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.dto.projet.CreateProjetDTO;
import com.scrumplateform.kante.exception.conception.ConceptionNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.model.cdcTechnique.CdcTechnique;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.projet.ProjetProjection;
import com.scrumplateform.kante.model.sprintPlanning.Sprint;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.model.lien.Lien;
import com.scrumplateform.kante.dto.sprint.SprintDetailDTO;

@Service
public interface ProjetServiceImpl {
    public void sendProjectAssignationNotification(String idUtilisateur) throws Exception;
    public Projet creerProjet(CreateProjetDTO projetDTO) throws Exception;
    public void initializeEtape(Projet projet) throws Exception;
    public List<ProjetProjection> getProjects(String scrumId, int etapeOrdre);
    public Page<ProjetProjection> getProjetsParMembreEquipe(String utilisateurId, String keyword, int step, int page, int size);
    public Projet updateSprintDevsInProject(String projetId, List<SprintDev> updatedSprintDevs) throws ProjectNotFoundException;
    public Projet updateSprintDevInDevTask(String projetId, List<SprintDev> updatedSprintDevs) throws ProjectNotFoundException;
    public Projet updateCdcTechniqueInProject(String projetId, CdcTechnique updatedCdcTechnique) throws ProjectNotFoundException;
    public Projet updateEquipeInProject(String projetId, List<Utilisateur> updatedEquipe) throws ProjectNotFoundException;
    public Projet updateSprintsInProject(String projetId, List<Sprint> updatedSprints) throws ProjectNotFoundException;
    public Projet updateConceptionInProject(String projetId, String conceptionId, Conception updatedConception) throws ProjectNotFoundException, ConceptionNotFoundException;
    public Page<Conception> getPaginatedConceptions(String projetId, int page, int size);
    public Projet addConceptionToProject(String projetId, Conception conception);
    public Projet updateTechnique(String projetId, Technique newTechnique);
    public Page<UserStory> getPaginatedUserStories(String projetId, int page, int size);
    public Projet updateUserStoryInProject(String projetId, String userStoryId, UserStory updatedUserStory);
    public Projet addUserStoryToProject(String projetId, UserStory userStory);
    public Projet getProjetById(String projetId);
    public Page<Projet> getPaginatedProjects(String scrumId, String keyword, int etapeOrdre, int page, int size);
    public Page<ProjetProjection> getPaginatedProjects(String scrumId, String keyword, int page, int size);
    public Projet updateLiensInProject(String projetId, List<Lien> updatedLiens) throws ProjectNotFoundException;
    public List<ProjetTechnoCount> getMostUsedTechnologies(int month, int year);
    public Page<SprintDetailDTO> getProjetSprints(String projetId, int page, int size);
    public SprintDevCheckPercentage getPercentageOfCompletedTask(String projetId);
    public byte[] exportTachesDevParMois(String userId, int month, int year) throws IOException;
    public byte[] exportCdcTechniquePdf(String projetId) throws IOException;
}
