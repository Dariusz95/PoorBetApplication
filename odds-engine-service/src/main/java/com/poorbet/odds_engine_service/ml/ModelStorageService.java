package com.poorbet.odds_engine_service.ml;

import com.poorbet.odds_engine_service.config.ModelProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelStorageService {

    private final ModelProperties modelProperties;

    public boolean modelExists() {
        Path path = modelProperties.getPath();
        
        return Files.exists(path);
    }
}