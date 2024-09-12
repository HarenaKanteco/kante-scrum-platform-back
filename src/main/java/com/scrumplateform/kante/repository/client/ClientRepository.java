package com.scrumplateform.kante.repository.client;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scrumplateform.kante.model.client.Client;

public interface ClientRepository extends MongoRepository<Client, String> {
    
}
