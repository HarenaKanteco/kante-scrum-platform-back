package com.scrumplateform.kante.service.etape;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;

@Service
public interface EtapeServiceImpl {
    @Transactional
    public Projet validerEtape(String projetId, Utilisateur utilisateurConnecte, int ordreActuel, int ordreSuivant) throws Exception;
}
