package com.ubs.expensemanager.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ubs.expensemanager.exception.BusinessException;

@Service
public class ReceiptStorageService {

    private final Path baseDir;

    public ReceiptStorageService(@Value("${app.receipts.storage-dir:receipts}") String dir) {
        this.baseDir = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new BusinessException("Could not create receipts storage directory");
        }
    }

    public String store(UUID expenseId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File is required");
        }

        String original = file.getOriginalFilename() == null ? "receipt" : file.getOriginalFilename();
        String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String ext = getExtension(safeOriginal);

        validateType(file.getContentType(), ext);

        String storedName = expenseId + "-" + Instant.now().toEpochMilli() + ext;
        Path target = baseDir.resolve(storedName).normalize();

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Failed to store receipt file");
        }

        return storedName;
    }

    public Resource loadAsResource(String storedName) {
        if (storedName == null || storedName.isBlank()) {
            throw new BusinessException("Receipt not found");
        }

        try {
            Path file = baseDir.resolve(storedName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException("Receipt not found");
            }
            return resource;
        } catch (Exception e) {
            throw new BusinessException("Receipt not found");
        }
    }

    private void validateType(String contentType, String ext) {
        String ct = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);

        boolean okByCt =
                ct.equals("application/pdf")
                        || ct.equals("image/png")
                        || ct.equals("image/jpeg");

        boolean okByExt =
                ext.equals(".pdf") || ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg");

        if (!okByCt && !okByExt) {
            throw new BusinessException("Only PDF, PNG or JPG receipts are allowed");
        }
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return "";
        return filename.substring(idx).toLowerCase(Locale.ROOT);
    }
}
