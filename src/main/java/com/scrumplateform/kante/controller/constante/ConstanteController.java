package com.scrumplateform.kante.controller.constante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.constante.Constante;
import com.scrumplateform.kante.service.constante.ConstanteService;

@RestController
@RequestMapping("/api/v1/constantes")
public class ConstanteController {

    @Autowired
    private ConstanteService constanteService;

    @GetMapping
    public ResponseEntity<Response> getConstante() {
        Response response = new Response();
        try {
            Constante constante = constanteService.getConstante();
            response.success(constante, "Constante récupérée avec succès.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la récupération de la constante : " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
