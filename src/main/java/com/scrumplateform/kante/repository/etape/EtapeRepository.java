package com.scrumplateform.kante.repository.etape;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scrumplateform.kante.model.etape.Etape;

public interface EtapeRepository extends MongoRepository<Etape, String> {
    Optional<Etape> findByOrdre(int ordre);
}

