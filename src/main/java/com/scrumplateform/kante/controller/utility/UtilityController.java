package com.scrumplateform.kante.controller.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.service.utility.EncryptionUtil;

@RestController
@RequestMapping("/api/v1/utility")
public class UtilityController {

    @PostMapping("/decrypt")
    public ResponseEntity<Response> decrypt(@RequestBody String encryption) {
        Response response = new Response();
        try {
            String decrypted = EncryptionUtil.decode(encryption);
            response.success(decrypted, "Decryption succeed");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.error(null, "Une erreur est survenue lors de la decryption");
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
