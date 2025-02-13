package com.scrumplateform.kante.service.notif;

import com.scrumplateform.kante.dto.notif.NotificationRequest;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessagingException;

@Service
@Slf4j
public class NotificationService2 {

    public void sendNotification(NotificationRequest request) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setToken(request.getToken())
            .setNotification(Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build())
            .putAllData(request.getData())
            .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notification envoyée avec succès: " + response);
    }

    public void sendNotificationToTopic(NotificationRequest request, String topic) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setTopic(topic)
            .setNotification(Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build())
            .putAllData(request.getData())
            .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notification topic envoyée avec succès: " + response);
    }
} 