package com.scrumplateform.kante.service.developpement;

import com.scrumplateform.kante.model.developpement.SprintContentDev;

public interface DeveloppementService {
    public void sendTaskNotification(SprintContentDev sprintContentDev, String projetId) throws Exception;
}
