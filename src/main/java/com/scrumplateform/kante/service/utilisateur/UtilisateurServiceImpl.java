package com.scrumplateform.kante.service.utilisateur;

import com.scrumplateform.kante.dto.account.LoginDTO;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import org.springframework.stereotype.Service;

@Service
public interface UtilisateurServiceImpl {
    public Utilisateur getUtilisateurById(String utilisateurId);
    public Utilisateur register(Utilisateur utilisateur);
    public Utilisateur login(LoginDTO loginDTO);
    public Utilisateur authenticate(LoginDTO loginDTO) throws Exception;
}
