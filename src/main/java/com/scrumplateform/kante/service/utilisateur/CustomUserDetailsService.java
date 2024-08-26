package com.scrumplateform.kante.service.utilisateur;

import com.scrumplateform.kante.model.security.CustomUserDetails;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.repository.utilisateur.UtilisateurRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent() == false) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec email: " + email);
        }
        return new CustomUserDetails(utilisateur.get());
    }
}

