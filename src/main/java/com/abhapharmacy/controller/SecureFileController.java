package com.abhapharmacy.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SecureFileController {

    @GetMapping({"/secure/images/{filename:.+}", "/files/prescriptions/{filename:.+}"})
    public ResponseEntity<Resource> serveImage(@PathVariable String filename, HttpSession session) {
        if (session.getAttribute("customerId") == null && session.getAttribute("staffId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Path file = Paths.get("app_data/prescriptions").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String lower = filename.toLowerCase();
                String contentType = MediaType.IMAGE_JPEG_VALUE;
                if (lower.endsWith(".png")) {
                    contentType = MediaType.IMAGE_PNG_VALUE;
                } else if (lower.endsWith(".pdf")) {
                    contentType = MediaType.APPLICATION_PDF_VALUE;
                } else if (lower.endsWith(".webp")) {
                    contentType = "image/webp";
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
