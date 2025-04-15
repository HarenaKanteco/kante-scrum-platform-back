package com.scrumplateform.kante.service;

import com.scrumplateform.kante.model.developpement.SprintDev;
import com.scrumplateform.kante.model.projet.Projet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class BurndownChartService {

    /**
     * 
     * @param projet Le projet pour lequel générer le burndown chart
     * @return Map contenant les données du burndown chart (date -> travail restant)
     */
    public Map<LocalDate, Integer> generateBurndownChartData(Projet projet) {
        // Vérifier si le projet a des sprints de développement
        if (projet.getSprintDevs() == null || projet.getSprintDevs().isEmpty()) {
            return new HashMap<>();
        }
        
        // Trouver la date de début et de fin pour l'itération en cours
        LocalDate dateDebut = getSprintStartDate(projet);
        LocalDate dateFin = getSprintEndDate(projet);
        if (dateDebut == null || dateFin == null) {
            return new HashMap<>();
        }
        
        // Calculer le travail total au début du sprint
        int travailTotal = calculateTotalWorkload(projet);
        
        // Initialiser le graphique avec la ligne idéale
        Map<LocalDate, Integer> burndownData = initializeIdealBurndown(dateDebut, dateFin, travailTotal);
        
        // Calculer le travail réel restant pour chaque jour
        calculateActualBurndown(projet, burndownData, travailTotal, dateDebut, dateFin);
        
        return burndownData;
    }
    
    /**
     * Obtient la date de début du sprint courant
     */
    private LocalDate getSprintStartDate(Projet projet) {
        return projet.getSprintDevs().stream()
                .filter(sprintDev -> sprintDev.getDateDebut() != null)
                .map(sprintDev -> convertToLocalDate(sprintDev.getDateDebut()))
                .min(LocalDate::compareTo)
                .orElse(null);
    }
    
    /**
     * Obtient la date de fin du sprint courant
     */
    private LocalDate getSprintEndDate(Projet projet) {
        // MODIFICATION: Utiliser d'abord les dates des sprints de développement
        LocalDate sprintEndDate = projet.getSprintDevs().stream()
                .filter(sprintDev -> sprintDev.getDateFin() != null)
                .map(sprintDev -> convertToLocalDate(sprintDev.getDateFin()))
                .max(LocalDate::compareTo)
                .orElse(null);
        
        // Si on n'a pas trouvé de date, utiliser la date de livraison prévue
        if (sprintEndDate == null && projet.getDateLivraisonPrevue() != null && !projet.getDateLivraisonPrevue().isEmpty()) {
            sprintEndDate = projet.getDateLivraisonPrevue().stream()
                    .map(this::convertToLocalDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);
        }
        
        // Si on n'a toujours pas de date, utiliser aujourd'hui + 2 semaines
        return sprintEndDate != null ? sprintEndDate : LocalDate.now().plusWeeks(2);
    }
    
    /**
     * Convertit la priorité en points selon les règles définies
     * - priorité 0 (basse) = 25 points
     * - priorité 100 (élevée) = 75 points
     * - priorité 50 (moyenne) reste à 50 points
     */
    private int convertPriorityToPoints(int priority) {
        if (priority == 0) return 25;      // Basse priorité
        if (priority == 100) return 75;    // Haute priorité
        return priority;                   // Autres valeurs (comme 50) restent inchangées
    }
    
    /**
     * Calcule la charge de travail totale basée sur toutes les tâches du sprint
     */
    private int calculateTotalWorkload(Projet projet) {
        int totalPoints = 0;
        
        for (SprintDev sprintDev : projet.getSprintDevs()) {
            if (sprintDev.getSprintContentDevs() != null) {
                totalPoints += sprintDev.getSprintContentDevs().stream()
                        // Utiliser la nouvelle méthode de conversion
                        .mapToInt(task -> convertPriorityToPoints(task.getPriorite()))
                        .sum();
            }
        }
        
        return Math.max(totalPoints, 1); // Valeur minimale de 1
    }
    
    /**
     * Initialise le graphique avec la ligne idéale (diminution régulière)
     */
    private Map<LocalDate, Integer> initializeIdealBurndown(LocalDate dateDebut, LocalDate dateFin, int travailTotal) {
        Map<LocalDate, Integer> burndownData = new TreeMap<>();
        
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        double pointsPerDay = (double) travailTotal / totalDays;
        
        LocalDate currentDate = dateDebut;
        while (!currentDate.isAfter(dateFin)) {
            long daysFromStart = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, currentDate);
            int idealPointsRemaining = (int) Math.round(travailTotal - (daysFromStart * pointsPerDay));
            idealPointsRemaining = Math.max(0, idealPointsRemaining);
            
            burndownData.put(currentDate, idealPointsRemaining);
            currentDate = currentDate.plusDays(1);
        }
        
        return burndownData;
    }
    
    /**
     * Calcule les données réelles du burndown en fonction des tâches terminées
     */
    private void calculateActualBurndown(Projet projet, Map<LocalDate, Integer> burndownData, 
            int travailTotal, LocalDate dateDebut, LocalDate dateFin) {
        
        System.out.println("Période du burndown chart : " + dateDebut + " à " + dateFin);
        System.out.println("Travail total à réaliser : " + travailTotal);
        
        // Créer une map pour suivre le travail terminé par jour
        Map<LocalDate, Integer> completedWorkByDay = new TreeMap<>();
        
        // Parcourir toutes les tâches terminées
        for (SprintDev sprintDev : projet.getSprintDevs()) {
            if (sprintDev.getSprintContentDevs() != null) {
                sprintDev.getSprintContentDevs().stream()
                    .filter(task -> task.getStatus() != null && task.getStatus().getStatus() == 10)
                    .filter(task -> task.getStatus().getCheckDate() != null)
                    .forEach(task -> {
                        LocalDate completionDate = convertToLocalDate(task.getStatus().getCheckDate());
                        
                        // Utiliser la même méthode de conversion pour les tâches terminées
                        int points = convertPriorityToPoints(task.getPriorite());
                        
                        System.out.println("Tâche terminée: " + task.getTitre() + 
                                          ", date: " + completionDate + 
                                          ", priorité: " + task.getPriorite() +
                                          ", points: " + points);
                        
                        // Ajouter toutes les tâches terminées
                        completedWorkByDay.merge(completionDate, points, Integer::sum);
                    });
            }
        }
        
        if (completedWorkByDay.isEmpty()) {
            System.out.println("Aucune tâche terminée trouvée dans la période");
            // Afficher au moins le point de départ
            burndownData.put(dateDebut, travailTotal);
            return;
        }
        
        System.out.println("Jours avec tâches terminées: " + completedWorkByDay.keySet());
        
        // Calculer le travail restant pour chaque jour
        Map<LocalDate, Integer> actualBurndown = new TreeMap<>();
        int remainingWork = travailTotal;
        
        // MODIFICATION: Couvrir toute la période du début à aujourd'hui
        LocalDate currentDate = dateDebut;
        LocalDate today = LocalDate.now();
        while (!currentDate.isAfter(today) && !currentDate.isAfter(dateFin)) {
            if (completedWorkByDay.containsKey(currentDate)) {
                remainingWork -= completedWorkByDay.get(currentDate);
                System.out.println("Jour " + currentDate + ": -" + completedWorkByDay.get(currentDate) + 
                                   " points, restant = " + Math.max(0, remainingWork));
            }
            actualBurndown.put(currentDate, Math.max(0, remainingWork));
            currentDate = currentDate.plusDays(1);
        }
        
        // Mettre à jour les données du burndown chart avec les valeurs réelles
        burndownData.putAll(actualBurndown);
    }
    
    /**
     * Convertit java.util.Date en java.time.LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Convertit java.time.LocalDateTime en java.time.LocalDate
     */
    private LocalDate convertToLocalDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    /**
     * Convertit java.time.LocalDateTime en java.util.Date
     */
    private Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
} 