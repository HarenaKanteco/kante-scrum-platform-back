package com.scrumplateform.kante.repository.projet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.scrumplateform.kante.model.projet.Projet;

public interface ProjetRepository extends MongoRepository<Projet, String> {

    @Query("{ 'scrum.id': ?0 }")
    Page<Projet> findByScrumId(String scrumId, Pageable pageable);

    @Query("{ 'scrum.id': ?0, $or: [ { 'client.email': { $regex: ?1, $options: 'i' } }, { 'client.entreprise.nom': { $regex: ?1, $options: 'i' } }, { 'titre': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }")
    Page<Projet> findByScrumIdAndKeyword(String scrumId, String keyword, Pageable pageable);
}
