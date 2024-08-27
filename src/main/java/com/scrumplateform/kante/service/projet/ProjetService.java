package com.scrumplateform.kante.service.projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.repository.projet.ProjetRepository;

@Service
public class ProjetService {

    @Autowired
    private ProjetRepository projetRepository;

    public Page<Projet> getPaginatedProjects(String scrumId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));

        // Constructing search criteria
        if (keyword == null || keyword.trim().isEmpty()) {
            return projetRepository.findByScrumId(scrumId, pageable);
        } else {
            String regex = ".*" + keyword + ".*";
            return projetRepository.findByScrumIdAndKeyword(scrumId, regex, pageable);
        }
    }
}
