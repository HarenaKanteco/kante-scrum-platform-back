package com.scrumplateform.kante.repository.utilisateur;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.security.Role;

public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findById(String id);
    Optional<Utilisateur> findByEmail(String email);
    @Query("{ 'roles': { $all: [?0, ?1] } }")
    List<Utilisateur> findByRolesContainingBoth(Role role1, Role role2);
}
