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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<Resource> getFile(
            @PathVariable String directory,
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDirectory, directory, subDirectory, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = Files.probeContentType(filePath);
                MediaType mediaType = contentType != null ?
                        MediaType.parseMediaType(contentType) :
                        MediaType.IMAGE_JPEG;

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
