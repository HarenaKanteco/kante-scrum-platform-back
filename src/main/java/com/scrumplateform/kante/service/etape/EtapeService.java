package com.scrumplateform.kante.service.etape;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumplateform.kante.exception.etape.EtapeNotFoundException;
import com.scrumplateform.kante.exception.etape.EtapeProjetNotFoundException;
import com.scrumplateform.kante.exception.projet.ProjectNotFoundException;
import com.scrumplateform.kante.model.constante.Constante;
import com.scrumplateform.kante.model.etape.Etape;
import com.scrumplateform.kante.model.etape.EtapeProjet;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.repository.etape.EtapeRepository;
import com.scrumplateform.kante.repository.projet.ProjetRepository;
import com.scrumplateform.kante.service.constante.ConstanteService;
import com.scrumplateform.kante.service.projet.ProjetServiceImpl;

@Service
public class EtapeService implements EtapeServiceImpl {

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private EtapeRepository etapeRepository;

    @Autowired
    private ConstanteService constanteService;

    @Autowired 
    private ProjetServiceImpl projetService;

    @Override
    @Transactional
    public Projet validerEtape(String projetId, Utilisateur utilisateurConnecte, int ordreActuel, int ordreSuivant) throws Exception {
        // Récupérer le projet par son ID
        Projet projet = projetRepository.findById(projetId)
            .orElseThrow(() -> new ProjectNotFoundException("Projet non trouvé"));

        // Trouver l'étape suivante
        Etape etapeSuivante = etapeRepository.findByOrdre(ordreSuivant)
            .orElseThrow(() -> new EtapeNotFoundException("Étape suivante non trouvée"));

        // Mettre à jour l'étape du projet
        EtapeProjet etapeActuelle = projet.getEtape();
        if (etapeActuelle != null) {
            etapeActuelle.setEtape(etapeSuivante);
            etapeActuelle.setDateValidation(new Date());
            etapeActuelle.setUtilisateur(utilisateurConnecte);
        } else {
            throw new IllegalStateException("L'étape actuelle du projet est nulle");
        }

        // Mettre à jour l'étape existante dans la liste etapes du projet
        Optional<EtapeProjet> etapeActuelleDansListe = projet.getEtapes().stream()
            .filter(et -> et.getEtape().getOrdre() == ordreActuel)
            .findFirst();

        if (etapeActuelleDansListe.isPresent()) {
            EtapeProjet etape = etapeActuelleDansListe.get();
            etape.setDateValidation(new Date());
            etape.setUtilisateur(utilisateurConnecte);
            // Pas besoin de sauvegarder séparément
        } else {
            throw new EtapeProjetNotFoundException("Étape avec ordre " + ordreActuel + " non trouvée dans les étapes du projet");
        }

        Constante constante = constanteService.getConstante();

        Optional<EtapeProjet> derniereEtape = projet.getEtapes().stream()
            .filter(et -> et.getEtape().getOrdre() == constante.getEtapeFinale())
            .findFirst();

        // Ajouter l'étape suivante à la liste des étapes du projet
        if(derniereEtape.isPresent() == false) {
            EtapeProjet nouvelleEtape = new EtapeProjet();
            nouvelleEtape.setEtape(etapeSuivante);
            nouvelleEtape.setUtilisateur(null);
            nouvelleEtape.setDateValidation(null);
            projet.getEtapes().add(nouvelleEtape);
        }

        if(etapeSuivante.getOrdre() == constante.getEtapeFinale() && derniereEtape.isPresent() == false) {
            for (Utilisateur ressource : projet.getEquipe()) {
                projetService.sendProjectAssignationNotification(ressource.getId());
            }
        }

        // Sauvegarder les modifications du projet
        return projetRepository.save(projet);
    }
}
