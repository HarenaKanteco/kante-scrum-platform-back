package com.scrumplateform.kante.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.directory}")
    private String uploadDirectory ;

    public String storeFile(String directory, MultipartFile file) throws IOException {
        String regex = "[-()=+!&#'\"*$ùéè\s+]";


            Path uploadPath = Paths.get(uploadDirectory+"/"+directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            byte[] bytes = file.getBytes();
            Path path = uploadPath.resolve( (UUID.randomUUID().toString()+"-"+file.getOriginalFilename().replaceAll(regex, "-") ).replaceAll("\\\\", "/"));
            Files.write(path, bytes);

            
            return path.toString().replaceAll("\\\\", "/").replaceFirst(".", "");
    }
}
