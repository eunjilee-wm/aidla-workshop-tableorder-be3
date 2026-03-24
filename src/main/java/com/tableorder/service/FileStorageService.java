package com.tableorder.service;

import com.tableorder.exception.InvalidFileException;
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
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String originalName = file.getOriginalFilename();
            String ext = originalName != null ? originalName.substring(originalName.lastIndexOf('.')) : "";
            String filename = UUID.randomUUID() + ext;

            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new InvalidFileException("Failed to store file: " + e.getMessage());
        }
    }

    public void delete(String filePath) {
        try {
            Path path = Paths.get(uploadDir).resolve(Paths.get(filePath).getFileName());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new InvalidFileException("Failed to delete file: " + e.getMessage());
        }
    }
}
