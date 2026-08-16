package com.vestingCustodyApp.vca.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class StorageService {

    @Value("${app.storage.dir}")
    private String storageDir;

    public String store(Long mediaId, MultipartFile file) throws IOException {
        Path folder = Path.of(storageDir, String.valueOf(mediaId));
        Files.createDirectories(folder);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (extension != null ? "." + extension : "");
        Path target = folder.resolve(fileName);
        file.transferTo(target);
        return target.toString();
    }
}
