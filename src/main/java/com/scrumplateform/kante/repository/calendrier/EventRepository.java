package com.scrumplateform.kante.repository.calendrier;

import com.scrumplateform.kante.model.calendrier.Evenement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Evenement, String> {
}
