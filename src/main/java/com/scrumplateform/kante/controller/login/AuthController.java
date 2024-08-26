package com.scrumplateform.kante.controller.login;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scrumplateform.kante.dto.account.LoginDTO;
import com.scrumplateform.kante.dto.account.exception.UserNotFoundException;
import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.model.utilisateur.Utilisateur;
import com.scrumplateform.kante.security.JwtService;
import com.scrumplateform.kante.security.Role;
import com.scrumplateform.kante.service.file.FileStorageService;
import com.scrumplateform.kante.service.utilisateur.UtilisateurService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register-scrum")
    public ResponseEntity<Response> register(@RequestParam String email,
                                              @RequestParam String password,
                                              @RequestParam MultipartFile photo) {
        Response response = new Response();
        try {
            String photoUrl = fileStorageService.storeFile("users/profiles", photo);
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setMotDePasse(password);
            utilisateur.setPhotoUrl(photoUrl);
            utilisateur.setRoles(EnumSet.of(Role.SCRUM));
            utilisateur.setDateModification(new Date());

            utilisateurService.register(utilisateur);

            response.success(utilisateur, "Utilisateur créé avec succès");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/register-dev")
    public ResponseEntity<Response> registerDev(@RequestParam String email,
                                              @RequestParam String password,
                                              @RequestParam MultipartFile photo) {
        Response response = new Response();
        try {
            String photoUrl = fileStorageService.storeFile("users/profiles", photo);
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setMotDePasse(password);
            utilisateur.setPhotoUrl(photoUrl);
            utilisateur.setRoles(EnumSet.of(Role.DEV));
            utilisateur.setDateModification(new Date());

            utilisateurService.register(utilisateur);

            response.success(utilisateur, "Utilisateur créé avec succès");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginDTO loginDTO) {
        Response response = new Response();
        try {
            // Authentifier l'utilisateur et générer le token
            Utilisateur utilisateur = utilisateurService.authenticate(loginDTO);
            String token = jwtService.generateToken(utilisateur);

            // Préparer la réponse avec le token
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("email", utilisateur.getEmail());
            result.put("roles", utilisateur.getRoles());
            result.put("photoUrl", utilisateur.getPhotoUrl());

            response.success(result, "Connecté avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            response.error(null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            response.error(null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
