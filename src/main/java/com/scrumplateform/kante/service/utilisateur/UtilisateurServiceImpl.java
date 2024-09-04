package com.scrumplateform.kante.service.utilisateur;

import com.scrumplateform.kante.dto.account.LoginDTO;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.security.Role;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface UtilisateurServiceImpl {
    public List<Utilisateur> getDevelopersWithSpecificRole(Role role);
    public Utilisateur getUtilisateurById(String utilisateurId);
    public Utilisateur register(Utilisateur utilisateur);
    public Utilisateur login(LoginDTO loginDTO);
    public Utilisateur authenticate(LoginDTO loginDTO) throws Exception;
}
