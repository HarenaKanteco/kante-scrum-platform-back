package com.scrumplateform.kante.repository.backlog;

import com.scrumplateform.kante.model.backlog.Backlog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BacklogRepository extends MongoRepository<Backlog, String> {
    Optional<Backlog> findByProjetId(String projetId);
    
    @Query("{ 'projetId': ?0, 'backlogItems.etat': ?1 }")
    Optional<Backlog> findByProjetIdAndBacklogItemsEtat(String projetId, String etat);
}
