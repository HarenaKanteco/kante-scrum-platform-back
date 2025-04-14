package com.scrumplateform.kante.service.utilisateur;

import com.scrumplateform.kante.dto.account.LoginDTO;
import com.scrumplateform.kante.exception.utilisateur.UserNotFoundException;
import com.scrumplateform.kante.model.technique.Technologie;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.model.utilisateur.UtilisateurEmail;
import com.scrumplateform.kante.repository.utilisateur.UtilisateurRepository;
import com.scrumplateform.kante.repository.projet.ProjetRepository;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.security.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService implements UtilisateurServiceImpl {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Utilisateur> getDevelopersWithSpecificRole(Role role, String projetId) {
        // Vérifiez que le rôle n'est pas SCRUM
        if (role == Role.SCRUM) {
            throw new IllegalArgumentException("Le rôle 'SCRUM' n'est pas autorisé.");
        }

        // Récupérez les utilisateurs ayant le rôle 'DEV' et le rôle spécifique
        List<Utilisateur> developers = utilisateurRepository.findByRolesContainingBoth(Role.DEV, role);
        
        // Si un ID de projet est fourni, triez les développeurs par technologies similaires
        if (projetId != null && !projetId.isEmpty()) {
            Optional<Projet> projetOptional = projetRepository.findById(projetId);
            
            if (projetOptional.isPresent()) {
                Projet projet = projetOptional.get();
                
                // Récupérer les technologies du projet
                List<Technologie> projetTechnologies;
                if (projet.getTechnique() != null) {
                    projetTechnologies = projet.getTechnique().getTechnologies();
                } else {
                    projetTechnologies = null;
                }

                if (projetTechnologies != null && !projetTechnologies.isEmpty()) {
                    // Trier les développeurs en fonction du nombre de technologies communes
                    developers.sort((dev1, dev2) -> {
                        int matchCount1 = countMatchingTechnologies(dev1.getTechnologies(), projetTechnologies);
                        int matchCount2 = countMatchingTechnologies(dev2.getTechnologies(), projetTechnologies);
                        return Integer.compare(matchCount2, matchCount1); // Ordre décroissant
                    });
                }
            }
        }

        return developers;
    }

    /**
     * Compte le nombre de technologies communes entre deux listes
     * @param devTechnologies Liste des technologies du développeur
     * @param projetTechnologies Liste des technologies du projet
     * @return Le nombre de technologies communes
     */
    private int countMatchingTechnologies(List<Technologie> devTechnologies, List<Technologie> projetTechnologies) {
        if (devTechnologies == null || projetTechnologies == null) {
            return 0;
        }
        
        int count = 0;
        for (Technologie devTech : devTechnologies) {
            for (Technologie projTech : projetTechnologies) {
                if (devTech.getId().equals(projTech.getId()) || 
                    (devTech.getLabel() != null && devTech.getLabel().equalsIgnoreCase(projTech.getLabel()))) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    @Override
    public Utilisateur getUtilisateurById(String utilisateurId) {
        return utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
    }

        public  List<UtilisateurEmail> getAllUser(){
        List<Utilisateur> listUsers = utilisateurRepository.findAll();
        List<UtilisateurEmail> listEmail = new ArrayList<>();

        for (Utilisateur user : listUsers){
            listEmail.add(new UtilisateurEmail(user.getEmail()));
        }
        return listEmail;
        }

    @Override
    public Utilisateur register(Utilisateur utilisateur) {
        // Hacher le mot de passe avant de sauvegarder l'utilisateur
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur login(LoginDTO loginDTO) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(loginDTO.getEmail());
        if (!utilisateurOptional.isPresent()) {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'email: " + loginDTO.getEmail());
        }

        Utilisateur utilisateur = utilisateurOptional.get();

        // Comparer le mot de passe avec le hachage stocké
        if (!passwordEncoder.matches(loginDTO.getPassword(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        return utilisateur; // Retourne l'utilisateur authentifié
    }

    @Override
    public Utilisateur authenticate(LoginDTO loginDTO) throws Exception {
        // Find user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + loginDTO.getEmail()));

        // Verify password
        if (!passwordEncoder.matches(loginDTO.getPassword(), utilisateur.getMotDePasse())) {
            throw new UserNotFoundException("Mot de passe incorrect");
        }

        return utilisateur;
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> all = utilisateurRepository.findAll();
        if (!all.isEmpty())
            return all;
        return null;
    }
}
