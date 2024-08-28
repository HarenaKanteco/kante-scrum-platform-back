package com.scrumplateform.kante.repository.constante;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scrumplateform.kante.model.constante.Constante;

public interface ConstanteRepository extends MongoRepository<Constante, String> {
    Constante findFirstBy(); 
}
