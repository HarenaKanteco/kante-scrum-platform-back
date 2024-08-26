package com.scrumplateform.kante.model.security;

import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir les rôles de l'utilisateur en autorités (GrantedAuthority)
        return utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Vous pouvez ajouter une logique pour vérifier si le compte a expiré
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Vous pouvez ajouter une logique pour vérifier si le compte est verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Vous pouvez ajouter une logique pour vérifier si les informations d'identification ont expiré
    }

    @Override
    public boolean isEnabled() {
        return true; // Vous pouvez ajouter une logique pour vérifier si l'utilisateur est activé
    }
}
