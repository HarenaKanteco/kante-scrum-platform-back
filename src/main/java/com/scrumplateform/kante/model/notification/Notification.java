package com.scrumplateform.kante.model.notification;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "notification")
public class Notification {
    private String id;
    private LocalDateTime date;
    private String contenu;
    private String idUtilisateur;
    private boolean vu = false;
    private LocalDateTime dateVu;
}
