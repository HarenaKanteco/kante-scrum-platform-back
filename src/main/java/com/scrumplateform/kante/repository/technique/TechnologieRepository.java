package com.scrumplateform.kante.repository.technique;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.scrumplateform.kante.model.technique.Technologie;

@Repository
public interface TechnologieRepository extends MongoRepository<Technologie, String> {
}
