package com.scrumplateform.kante.service.developpement;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.scrumplateform.kante.model.constante.Constante;
import com.scrumplateform.kante.model.developpement.SprintContentDev;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.service.constante.ConstanteService;
import com.scrumplateform.kante.service.email.EmailService;
import com.scrumplateform.kante.service.projet.ProjetService;
import com.scrumplateform.kante.service.utility.EncryptionUtil;

@Service
public class DeveloppementServiceImpl implements DeveloppementService {

    @Autowired
    EmailService emailService;

    @Autowired
    ProjetService projetService;

    @Autowired
    ConstanteService constanteService;

    public void sendTaskNotification(SprintContentDev sprintContentDev, String projectId) throws Exception {
        Constante constante = constanteService.getConstante();
        String baseUrl = constante.getEnvironnement().getBaseUrl();
        Context context = new Context();    
        Projet projet = projetService.getProjetById(projectId);
        context.setVariable("projectName", projet.getTitre());
        context.setVariable("title", sprintContentDev.getTitre());
        context.setVariable("description", sprintContentDev.getDescription());
        context.setVariable("scrumMail", projet.getScrum().getEmail());
        context.setVariable("userName", sprintContentDev.getResponsable().getEmail());

        // Encrypt the task ID
        String encryptedTaskId = EncryptionUtil.encode(sprintContentDev.getId());
        String taskLink = baseUrl+"/dev/development/" + projectId + "?taskId=" + encryptedTaskId;
        context.setVariable("taskLink", taskLink);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.FRENCH);

        if(sprintContentDev.getDateDebut() != null) {
            String formattedDateDebut = dateFormat.format(sprintContentDev.getDateDebut());
            context.setVariable("dateDebut", formattedDateDebut);
        } else {
            context.setVariable("dateDebut", "non définie");
        }

        if(sprintContentDev.getDateFin() != null) {
            String formattedDateFin = dateFormat.format(sprintContentDev.getDateFin());
            context.setVariable("dateFin", formattedDateFin);
        } else {
            context.setVariable("dateFin", "non définie");
        }

        emailService.sendEmail(sprintContentDev.getResponsable().getEmail(), "Assignation de tâche", "developpement/notif-assignation-tache", context);
    }
}
