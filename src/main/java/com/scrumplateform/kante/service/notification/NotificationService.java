package com.scrumplateform.kante.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.model.notification.Notification;
import com.scrumplateform.kante.repository.notification.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService implements NotificationServiceImpl {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    public Notification markAsViewed(String notificationId) throws Exception {
        // Récupérer la notification
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification non trouvée"));

        // Marquer comme vue
        notification.setVu(true);
        notification.setDateVu(LocalDateTime.now());

        // Sauvegarder la notification mise à jour
        return notificationRepository.save(notification);
    }

    @Override
    public Notification createNotification(String idUtilisateur, String contenu) throws Exception {
        Notification notification = new Notification();
        notification.setIdUtilisateur(idUtilisateur);
        notification.setContenu(contenu);
        notification.setDate(LocalDateTime.now());
        notification.setVu(false);  // La notification est initialement non vue
        notification.setDateVu(null); // Pas encore vue

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUnseenNotificationsByUtilisateur(String idUtilisateur) {
        return notificationRepository.findUnseenByUtilisateur(idUtilisateur);
    }
}
