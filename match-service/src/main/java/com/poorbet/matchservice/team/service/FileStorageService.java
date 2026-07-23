package com.poorbet.matchservice.team.service;

import com.poorbet.matchservice.config.ImagesProperties;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg");
    private static final long MAX_SIZE_BYTES = 1024 * 1024;

    private static final int TARGET_DIMENSION_PX = 256;
    private static final double OUTPUT_QUALITY = 0.85;

    private final Path storageDir;

    public FileStorageService(ImagesProperties properties) {
        this.storageDir = Path.of(properties.getPath());
    }

    public String store(UUID teamId, MultipartFile file) {
        validateFile(file);

        try {
            Files.createDirectories(storageDir);
            String filename = teamId + resolveExtension(file.getContentType());
            Path target = storageDir.resolve(filename);

            Thumbnails.of(file.getInputStream())
                    .size(TARGET_DIMENSION_PX, TARGET_DIMENSION_PX)
                    .outputQuality(OUTPUT_QUALITY)
                    .toFile(target.toFile());

            return filename;
        } catch (IOException e) {
            throw new UncheckedIOException("Nie udało się zapisać pliku logo drużyny", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Plik jest pusty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Dozwolone formaty: PNG, JPEG");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Maksymalny rozmiar pliku to 1 MB");
        }
    }

    private String resolveExtension(String contentType) {
        return "image/png".equals(contentType) ? ".png" : ".jpg";
    }
}
