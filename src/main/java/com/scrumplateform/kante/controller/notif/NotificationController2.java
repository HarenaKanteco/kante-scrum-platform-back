package com.scrumplateform.kante.controller.notif;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scrumplateform.kante.dto.notif.NotificationRequest;
import com.scrumplateform.kante.service.notif.NotificationService2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController2 {

    private final NotificationService2 notificationService2;

    public NotificationController2(NotificationService2 notificationService2) {
        this.notificationService2 = notificationService2;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            notificationService2.sendNotification(request);
            return ResponseEntity.ok("Notification envoyée avec succès");
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de l'envoi de la notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de l'envoi de la notification");
        }
    }

    @PostMapping("/topic/{topic}")
    public ResponseEntity<String> sendNotificationToTopic(
            @RequestBody NotificationRequest request,
            @PathVariable String topic) {
        try {
            notificationService2.sendNotificationToTopic(request, topic);
            return ResponseEntity.ok("Notification topic envoyée avec succès");
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de l'envoi de la notification topic", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de l'envoi de la notification topic");
        }
    }
} 