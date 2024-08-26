package com.scrumplateform.kante.model.utilisateur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import com.scrumplateform.kante.security.Role;

import java.util.Date;
import java.util.EnumSet;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "utilisateur")
public class Utilisateur {

    private String id;

    private String email;

    private String motDePasse;

    private EnumSet<Role> roles;

    private Date dateModification;

    private String photoUrl;
}
