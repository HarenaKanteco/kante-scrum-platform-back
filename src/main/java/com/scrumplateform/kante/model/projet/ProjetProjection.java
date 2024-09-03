package com.scrumplateform.kante.model.projet;

import com.scrumplateform.kante.model.client.Client;
import com.scrumplateform.kante.model.etape.EtapeProjet;

public interface ProjetProjection {
    String getId();
    Client getClient();
    String getTitre();
    String getDescription();
    EtapeProjet getEtape();
}
