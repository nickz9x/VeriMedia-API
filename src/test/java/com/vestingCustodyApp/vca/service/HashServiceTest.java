package com.vestingCustodyApp.vca.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    void sameBytesProduceSameHash(){
        String first = hashService.sha256("conteudo".getBytes(StandardCharsets.UTF_8));
        String second = hashService.sha256("conteudo".getBytes(StandardCharsets.UTF_8));

        assertEquals(first, second);
    }

    @Test
    void differentBytesProduceDifferentHash(){
        String first = hashService.sha256("a".getBytes(StandardCharsets.UTF_8));
        String second = hashService.sha256("b".getBytes(StandardCharsets.UTF_8));

        assertNotEquals(first, second);
    }

    @Test
    void hashIsAlways64HexCharacters(){
        String hash = hashService.sha256(new byte[0]);

        assertEquals(64, hash.length());
    }

    @Test
    void matchesReturnsTrueForIdenticalContent(){
        byte[] bytes = "arquivo".getBytes(StandardCharsets.UTF_8);
        String stored = hashService.sha256(bytes);

        assertTrue(hashService.matches(bytes, stored));
    }

    @Test
    void matchesReturnsFalseForTamperedContent(){
        byte[] original = "arquivo original".getBytes(StandardCharsets.UTF_8);
        String stored = hashService.sha256(original);
        byte[] tampered = "arquivo alterado".getBytes(StandardCharsets.UTF_8);

        assertFalse(hashService.matches(tampered, stored));
    }
}
