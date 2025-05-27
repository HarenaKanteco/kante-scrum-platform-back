package com.scrumplateform.kante.controller.file;

import com.scrumplateform.kante.http.response.Response;
import com.scrumplateform.kante.service.file.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("api/file")
public class FileController {
    @Value("${upload.directory}")
    private String uploadDirectory;
    
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Response> uploadImage(@RequestParam MultipartFile file) {
        Response response = new Response();
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String photoUrl = fileStorageService.storeFileSprint("sprint/img", file);
            response.setData(photoUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/uploads/{directory}/{subDirectory}/{filename:.+}")
    public ResponseEntity<?> getFile(
            @PathVariable String directory,
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", "https://res.cloudinary.com/doqpbbuov/" + directory + "/" + subDirectory + "/" + filename)
                .build();
    }
}
