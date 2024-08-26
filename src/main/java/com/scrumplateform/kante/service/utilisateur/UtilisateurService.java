package com.scrumplateform.kante.service.utilisateur;

import com.scrumplateform.kante.dto.account.LoginDTO;
import com.scrumplateform.kante.exception.utilisateur.UserNotFoundException;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.repository.utilisateur.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur register(Utilisateur utilisateur) {
        // Hacher le mot de passe avant de sauvegarder l'utilisateur
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }

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
}
