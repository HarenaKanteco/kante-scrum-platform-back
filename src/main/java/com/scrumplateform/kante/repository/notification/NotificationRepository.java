package com.scrumplateform.kante.repository.notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.scrumplateform.kante.model.notification.Notification;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query("{ 'idUtilisateur': ?0, 'vu': false }")
    List<Notification> findUnseenByUtilisateur(String idUtilisateur);
}
