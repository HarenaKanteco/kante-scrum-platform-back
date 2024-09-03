package com.scrumplateform.kante.service.projet;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.exception.conception.ConceptionNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.model.conception.Conception;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.projet.ProjetProjection;
import com.scrumplateform.kante.model.sprintPlanning.Sprint;
import com.scrumplateform.kante.model.technique.Technique;
import com.scrumplateform.kante.model.userStory.UserStory;

@Service
public interface ProjetServiceImpl {
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
}
