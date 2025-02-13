package com.scrumplateform.kante.model.calendrier;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Evenement {
    @Id
    private String id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private List<String> participants;
    private String type;
    private String priority;
}
