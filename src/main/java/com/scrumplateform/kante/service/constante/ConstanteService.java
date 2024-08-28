package com.scrumplateform.kante.service.constante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scrumplateform.kante.model.constante.Constante;
import com.scrumplateform.kante.repository.constante.ConstanteRepository;

@Service
public class ConstanteService {

    @Autowired
    private ConstanteRepository constanteRepository;

    public Constante getConstante() {
        return constanteRepository.findFirstBy();
    }
}
