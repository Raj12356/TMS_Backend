package com.tms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No file provided"));
        }

        try {
            // Ensure uploads directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload";
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String uniqueName = System.currentTimeMillis() + "_" + safeName;
            Path filePath = uploadPath.resolve(uniqueName);

            file.transferTo(filePath.toFile());

            // Also copy to frontend public/uploads if frontend directory exists for joint dev
            Path frontendUploads = Paths.get("../frontend/public/uploads");
            if (Files.exists(Paths.get("../frontend/public"))) {
                if (!Files.exists(frontendUploads)) {
                    Files.createDirectories(frontendUploads);
                }
                Files.copy(filePath, frontendUploads.resolve(uniqueName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("url", "/uploads/" + uniqueName);
            resp.put("fileName", originalName);
            resp.put("size", file.getSize());
            resp.put("type", file.getContentType());

            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}

