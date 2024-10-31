package com.scrumplateform.kante.service.statistique;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.model.developpement.SprintContentDev;
import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.sprintCheck.SprintCheck;
import com.scrumplateform.kante.model.statistique.StatistiqueScrum;
import com.scrumplateform.kante.service.projet.ProjetService;

@Service
public class StatistiqueService implements StatistiqueServiceImpl {

    @Autowired
    ProjetService projetService;

    @Override
    public StatistiqueScrum getAllScrumStatistiques(String projetId) {
        StatistiqueScrum statistiqueScrum = new StatistiqueScrum();

        // Récupérer les statistiques générales
        Map<String, Object> statistiquesGenerales = getStatistiquesGenerales(projetId);
        statistiqueScrum.setStatistiquesGenerales(statistiquesGenerales);

        // Récupérer les statistiques sur les tâches
        Map<String, Object> statistiquesTaches = getStatistiquesTaches(projetId);
        statistiqueScrum.setStatistiquesTaches(statistiquesTaches);

        // Récupérer les statistiques de performance
        Map<String, Object> statistiquesPerformance = getPerformanceStatistiques(projetId);
        statistiqueScrum.setStatistiquesPerformance(statistiquesPerformance);

        // Récupérer l'analyse temporelle
        Map<String, Object> analyseTemporelle = getAnalyseTemporelle(projetId);
        statistiqueScrum.setAnalyseTemporelle(analyseTemporelle);

        return statistiqueScrum;
    }

    public Map<String, Long> getTachesParDeveloppeur(String projetId) {
        Projet projet = projetService.getProjetById(projetId);

        // Initialiser un compteur pour chaque développeur
        Map<String, Long> repartition = new HashMap<>();
        
        // Parcourir chaque SprintDev du projet
        for (SprintDev sprintDev : projet.getSprintDevs()) {
            // Pour chaque tâche dans le sprint
            for (SprintContentDev task : sprintDev.getSprintContentDevs()) {
                String emailDev = task.getResponsable().getEmail();
                repartition.put(emailDev, repartition.getOrDefault(emailDev, 0L) + 1); 
            }
        }

        return repartition; // Retourner le nombre de tâches par développeur
    }

    public Map<String, Long> getTachesPondereesParDeveloppeur(String projetId) {
        Projet projet = projetService.getProjetById(projetId);

        Map<String, Long> repartitionPonderee = new HashMap<>();
        
        // Parcourir chaque SprintDev du projet
        for (SprintDev sprintDev : projet.getSprintDevs()) {
            for (SprintContentDev task : sprintDev.getSprintContentDevs()) {
                String emailDev = task.getResponsable().getEmail();
                repartitionPonderee.put(emailDev, repartitionPonderee.getOrDefault(emailDev, 0L) + task.getDifficulte()); // Ajouter la difficulté à la pondération
            }
        }

        return repartitionPonderee; // Retourner le score pondéré par développeur
    }

    @Override
    public Map<String, Object> getAnalyseTemporelle(String projetId) {
        Projet projet = projetService.getProjetById(projetId);

        List<SprintDev> sprints = projet.getSprintDevs();

        sprints = sprints != null ? sprints : new ArrayList<>();
        
        long totalDuration = 0;
        long totalTasks = 0;
        Map<String, Long> sprintDurations = new HashMap<>();
        int tasksInDelay = 0;

        LocalDateTime now = LocalDateTime.now();

        // Parcourir les sprints pour calculer les durées
        for (SprintDev sprint : sprints) {
            long sprintDuration = 0;
            List<SprintContentDev> sprintContentDevs = sprint.getSprintContentDevs();
            for (SprintContentDev task : sprintContentDevs) {
                if (task.getDateDebut() != null && task.getDateFin() != null) {
                    // Convertir LocalDateTime à Instant pour calculer la durée
                    LocalDateTime start = task.getDateDebut().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime end = task.getDateFin().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    Duration duration = Duration.between(start, end);

                    totalDuration += duration.toMinutes();
                    totalTasks++;

                    // Vérifier les tâches en retard
                    if (task.getStatus() == null || task.getStatus().getStatus() != 10) {
                        if (end.isBefore(now)) {
                            tasksInDelay++;
                        }
                    }
                    
                    sprintDuration += duration.toMinutes();
                }
            }
            sprintDurations.put(sprint.getTitre(), sprintDuration);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDurationMinutes", totalDuration);
        stats.put("totalTasks", totalTasks);
        stats.put("sprintDurations", sprintDurations);
        stats.put("tasksInDelay", tasksInDelay);

        return stats;
    }

    @Override
    public Map<String, Object> getPerformanceStatistiques(String projetId) {
        Projet projet = projetService.getProjetById(projetId);
        List<SprintDev> sprints = projet.getSprintDevs();

        sprints = sprints != null ? sprints : new ArrayList<>();
        
        int nombreTotalTaches = 0;
        int nombreTachesCompletes = 0;
        int nombreTachesRetard = 0;
        long totalCompletionTime = 0;

        for (SprintDev sprint : sprints) {
            List<SprintContentDev> sprintContentDevs = sprint.getSprintContentDevs();
            nombreTotalTaches += sprintContentDevs.size();

            for (SprintContentDev task : sprintContentDevs) {
                SprintCheck status = task.getStatus();
                if (status != null && status.getStatus() == 10) {
                    nombreTachesCompletes++;

                    // Calculate task completion time (time between task start and checkDate)
                    if (task.getDateDebut() != null && status.getCheckDate() != null) {
                        Instant taskStartInstant = task.getDateDebut().toInstant(); // Convert Date to Instant
                        Instant checkDateInstant = status.getCheckDate().atZone(ZoneId.systemDefault()).toInstant();
                        
                        long taskCompletionTime = Duration.between(taskStartInstant, checkDateInstant).toMillis();
                        totalCompletionTime += taskCompletionTime;
                    }
                } else {
                    // Check for delay
                    Instant now = Instant.now(); // Current time as Instant
                    if (task.getDateFin() != null) {
                        Instant taskFinInstant = task.getDateFin().toInstant(); // Convert Date to Instant

                        if (taskFinInstant.isBefore(now)) {
                            nombreTachesRetard++;
                        }
                    }
                }
            }
        }

        // Calculate average task completion time (in milliseconds)
        double averageCompletionTime = nombreTachesCompletes > 0 ? (double) totalCompletionTime / nombreTachesCompletes : 0;

        // Prepare statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreTotalTaches", nombreTotalTaches);
        stats.put("nombreTachesCompletes", nombreTachesCompletes);
        stats.put("nombreTachesRetard", nombreTachesRetard);
        stats.put("averageCompletionTime", averageCompletionTime); // Time in milliseconds
        stats.put("taskCompletionRate", (double) nombreTachesCompletes / nombreTotalTaches * 100);
        stats.put("delayRate", (double) nombreTachesRetard / nombreTotalTaches * 100);

        return stats;
    }

    @Override
    public Map<String, Object> getStatistiquesTaches(String projetId) {
        Projet projet = projetService.getProjetById(projetId);
        List<SprintDev> sprints = projet.getSprintDevs();

        sprints = sprints != null ? sprints : new ArrayList<>();

        int totalTaches = 0;
        int totalTachesCompletes = 0;
        int totalTachesIncompletes = 0;
        long totalDureeTaches = 0;
        int nombreTachesRetard = 0;

        // Pour chaque sprint, on parcourt les tâches
        for (SprintDev sprint : sprints) {
            List<SprintContentDev> sprintContentDevs = sprint.getSprintContentDevs();
            totalTaches += sprintContentDevs.size();

            for (SprintContentDev task : sprintContentDevs) {
                SprintCheck status = task.getStatus();
                
                // Comptabiliser les tâches terminées et incomplètes
                if (status != null && status.getStatus() == 10) {
                    totalTachesCompletes++;
                    // Calculer la durée de la tâche en jours
                    if (task.getDateDebut() != null && task.getDateFin() != null) {
                        Duration duration = Duration.between(task.getDateDebut().toInstant(), task.getDateFin().toInstant());
                        totalDureeTaches += duration.toDays();
                    }
                } else {
                    totalTachesIncompletes++;
                }

                // Vérifier les retards (tâche non terminée après dateFin)
                if (status == null || status.getStatus() != 10) {
                    LocalDateTime now = LocalDateTime.now();
                    if (task.getDateFin() != null) {
                        Instant taskFinInstant = task.getDateFin().toInstant();
                        Instant nowInstant = now.atZone(ZoneId.systemDefault()).toInstant();
                
                        if (taskFinInstant.isBefore(nowInstant)) {
                            nombreTachesRetard++;
                        }
                    }
                }                
            }
        }

        // Calcul de la durée moyenne des tâches
        long dureeMoyenneTaches = totalTachesCompletes > 0 ? totalDureeTaches / totalTachesCompletes : 0;

        // Préparer les résultats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTaches", totalTaches);
        stats.put("totalTachesCompletes", totalTachesCompletes);
        stats.put("totalTachesIncompletes", totalTachesIncompletes);
        stats.put("dureeMoyenneTaches", dureeMoyenneTaches);
        stats.put("nombreTachesRetard", nombreTachesRetard);

        return stats;
    }

    @Override
    public Map<String, Object> getStatistiquesGenerales(String projetId) {
        Projet projet = projetService.getProjetById(projetId);

        List<SprintDev> sprints = projet.getSprintDevs();
        
        int nombreTotalSprints = sprints != null ? sprints.size() : 0;
        int nombreTotalTaches = 0;
        int nombreTachesCompletes = 0;

        // Parcourir les sprints pour compter les tâches
        if(sprints != null) {
            for (SprintDev sprint : sprints) {
                List<SprintContentDev> sprintContentDevs = sprint.getSprintContentDevs();
                nombreTotalTaches += sprintContentDevs.size();
    
                // Compter les tâches terminées (status = 10)
                for (SprintContentDev task : sprintContentDevs) {
                    SprintCheck status = task.getStatus();
                    if (status != null && status.getStatus() == 10) {
                        nombreTachesCompletes++;
                    }
                }
            }
        }

        // Préparer les résultats sous forme de Map
        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreTotalSprints", nombreTotalSprints);
        stats.put("nombreTotalTaches", nombreTotalTaches);
        stats.put("nombreTachesCompletes", nombreTachesCompletes);

        return stats;
    }
    
}
