package com.vestingCustodyApp.vca.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeCreatesFolderPerMediaAndSavesFileWithGeneratedName() throws Exception {
        StorageService storageService = new StorageService();
        ReflectionTestUtils.setField(storageService, "storageDir", tempDir.toString());

        byte[] content = "conteudo-binario".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", content);

        String path = storageService.store(15L, file);

        Path storedPath = Path.of(path);
        assertTrue(Files.exists(storedPath));
        assertEquals(tempDir.resolve("15"), storedPath.getParent());
        assertTrue(storedPath.getFileName().toString().endsWith(".jpg"));
        assertNotEquals("foto.jpg", storedPath.getFileName().toString());
        assertArrayEquals(content, Files.readAllBytes(storedPath));
    }

    @Test
    void storeHandlesFileWithoutExtension() throws Exception {
        StorageService storageService = new StorageService();
        ReflectionTestUtils.setField(storageService, "storageDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile("file", "arquivo", "application/octet-stream", "abc".getBytes());

        String path = storageService.store(7L, file);

        assertTrue(Files.exists(Path.of(path)));
        assertFalse(Path.of(path).getFileName().toString().contains("."));
    }
}
