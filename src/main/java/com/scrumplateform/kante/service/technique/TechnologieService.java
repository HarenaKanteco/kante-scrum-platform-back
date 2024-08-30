package com.scrumplateform.kante.service.technique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scrumplateform.kante.model.technique.Technologie;
import com.scrumplateform.kante.repository.technique.TechnologieRepository;

import java.util.List;

@Service
public class TechnologieService {

    @Autowired
    private TechnologieRepository technologieRepository;

    public List<Technologie> getAllTechnologies() {
        return technologieRepository.findAll();
    }
}
