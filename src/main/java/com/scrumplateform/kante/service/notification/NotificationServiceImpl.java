package com.scrumplateform.kante.service.notification;

import org.springframework.stereotype.Service;

import com.scrumplateform.kante.model.notification.Notification;
import java.util.List;

@Service
public interface NotificationServiceImpl {
    public Notification markAsViewed(String notificationId) throws Exception;
    public Notification createNotification(String idUtilisateur, String contenu) throws Exception;
    public List<Notification> getUnseenNotificationsByUtilisateur(String idUtilisateur);
}
