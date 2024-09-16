package com.scrumplateform.kante.dto.projet;

import java.util.List;
import java.util.Date;

import com.scrumplateform.kante.model.lien.Lien;

import lombok.Data;

@Data
public class CreateProjetDTO {
    private String scrumId; 
    private String clientId; 
    private String titre;
    private String description;
    private List<Lien> liens; 
    private List<Date> dateLivraisonPrevue; 
}
