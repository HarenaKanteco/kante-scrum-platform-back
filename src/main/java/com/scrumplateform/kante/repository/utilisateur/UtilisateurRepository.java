package com.scrumplateform.kante.repository.utilisateur;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scrumplateform.kante.model.utilisateur.Utilisateur;

public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByEmail(String email);
}
