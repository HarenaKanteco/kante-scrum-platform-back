package com.scrumplateform.kante.controller.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.notification.Notification;
import com.scrumplateform.kante.service.notification.NotificationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationServiceImpl notificationService;

    @PutMapping("/mark-as-viewed/{notificationId}")
    public ResponseEntity<Response> marquerNotificationCommeVue(@PathVariable String notificationId) {
        Response response = new Response();
        try {
            // Marquer la notification comme vue
            Notification notification = notificationService.markAsViewed(notificationId);

            // Réponse de succès
            response.success(notification, "Notification marquée comme vue");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // En cas d'erreur
            response.error(null, "Erreur lors de la mise à jour de la notification");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unseen")
    public ResponseEntity<Response> getUnseenNotifications(
            @RequestParam("idUtilisateur") String idUtilisateur) {
        
        Response response = new Response();
        
        try {
            // Récupérer les notifications non vues pour l'utilisateur donné
            List<Notification> notifications = notificationService.getUnseenNotificationsByUtilisateur(idUtilisateur);

            // Vérifier si des notifications existent
            if (notifications.isEmpty()) {
                response.success(notifications, "Aucune notification non vue.");
            } else {
                response.success(notifications, "Notifications non vues récupérées avec succès.");
            }

            // Retourner la réponse avec un statut HTTP 200 OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Gestion d'erreur et réponse avec statut HTTP 500 Internal Server Error
            response.error(null, "Erreur lors de la récupération des notifications.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

