package com.scrumplateform.kante.repository.projet;

import java.util.Date;
import java.util.List;

import com.scrumplateform.kante.dto.projet.ProjetSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.scrumplateform.kante.model.projet.Projet;
import com.scrumplateform.kante.model.projet.ProjetProjection;

public interface ProjetRepository extends MongoRepository<Projet, String> {
    @Query("{ 'scrum.id': ?0, 'etape.etape.ordre': ?1 }")
    List<ProjetProjection> findByScrumIdAndEtapeOrdre(String scrumId, int etapeOrdre, Sort sort);

    @Query(value = "{'equipe': { $elemMatch: { '_id': ?0 } }, 'etape.etape.ordre': ?1 }", fields = "{ 'id': 1, 'client': 1, 'titre': 1, 'description': 1, 'etape': 1 }")
    Page<ProjetProjection> findByEquipeAndEtapeOrdre(String id, int etapeOrdre, Pageable pageable);

    @Query(value = "{'equipe': { $elemMatch: { '_id': ?0 } }, 'etape.etape.ordre': ?2 , $or: [ { 'client.email': { $regex: ?1, $options: 'i' } }, { 'client.entreprise.nom': { $regex: ?1, $options: 'i' } }, { 'titre': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }", fields = "{ 'id': 1, 'client': 1, 'titre': 1, 'description': 1, 'etape': 1 }")
    Page<ProjetProjection> findByEquipeAndKeywordAndEtapeOrdre(String id, String keyword, int etapeOrdre, Pageable pageable);

    @Query("{ 'scrum.id': ?0, 'etape.etape.ordre': ?1 }")
    Page<Projet> findByScrumIdAndEtapeOrdre(String scrumId, int etapeOrdre, Pageable pageable);

    @Query("{ 'scrum.id': ?0, 'etape.etape.ordre': ?2, $or: [ { 'client.email': { $regex: ?1, $options: 'i' } }, { 'client.entreprise.nom': { $regex: ?1, $options: 'i' } }, { 'titre': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }")
    Page<Projet> findByScrumIdAndKeywordAndEtapeOrdre(String scrumId, String keyword, int etapeOrdre, Pageable pageable);

    @Query(value = "{ 'scrum.id': ?0 }", fields = "{ 'id': 1, 'client': 1, 'titre': 1, 'description': 1, 'etape': 1 }")
    Page<ProjetProjection> findByScrumId(String scrumId, Pageable pageable);

    @Query(value = "{ 'scrum.id': ?0, $or: [ { 'client.email': { $regex: ?1, $options: 'i' } }, { 'client.entreprise.nom': { $regex: ?1, $options: 'i' } }, { 'titre': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }", fields = "{ 'id': 1, 'client': 1, 'titre': 1, 'description': 1, 'etape': 1 }")
    Page<ProjetProjection> findByScrumIdAndKeyword(String scrumId, String keyword, Pageable pageable);

    @Query(value = "{ 'dateCreation': { $gte: ?0, $lt: ?1 } }", 
           fields = "{ 'technique.technologies.label': 1 }")
    List<Projet> findTechnologiesByDateRange(Date startDate, Date endDate);

    @Query("{ 'equipe.id' : ?0 }")
    List<Projet> findByEq(String userId);

    @Query(value = "{ }", 
           fields = "{ 'id': 1, 'titre': 1, 'client.logo': 1 }")
    List<ProjetSimpleDTO> findAllProjectionsSimple();

    @Query(value = "{}", fields = "{ 'id': 1, 'titre': 1, 'client': 1 }")
    List<Projet> findAllProjetsSimple();
}
