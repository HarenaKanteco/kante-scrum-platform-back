package com.scrumplateform.kante.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class FileStorageService {

    @Value("${upload.directory}")
    private String uploadDirectory;
    
    private final Cloudinary cloudinary;
    
    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public String storeFile(String directory, MultipartFile file) throws IOException {
        return uploadToCloudinary(file, directory);
    }

    public String storeFileSprint(String directory, MultipartFile file) throws IOException {
        return uploadToCloudinary(file, directory);
    }
    
    private String uploadToCloudinary(MultipartFile file, String folder) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto",
                "unique_filename", true
        );
        
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    public Path getFilePath(String directory, String filename) {
        return Paths.get(uploadDirectory, directory, filename);
    }

}
