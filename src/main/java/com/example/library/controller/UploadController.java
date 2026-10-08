package com.example.library.controller;

import com.example.library.dto.CoverUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private final Path uploadDir;

    public UploadController(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @PostMapping("/covers")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public CoverUploadResponse uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cover file cannot be empty");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Cover file cannot exceed 2MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only jpg, png, webp or gif cover images are allowed");
        }
        Files.createDirectories(uploadDir.resolve("covers"));
        String extension = extensionOf(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve("covers").resolve(filename).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid upload path");
        }
        file.transferTo(target);
        return new CoverUploadResponse("/uploads/covers/" + filename);
    }

    private String extensionOf(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif").contains(extension)) {
                return extension;
            }
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
