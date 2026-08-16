package com.vestingCustodyApp.vca.service;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HashService {

    public String sha256(byte[] bytes){
        return HexFormat.of().formatHex(sha256Bytes(bytes));
    }

    public byte[] sha256Bytes(byte[] bytes){
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available in this JVM", e);
        }
    }

    public boolean matches(byte[] fileBytes, String storedHex){
        byte[] storedBytes = HexFormat.of().parseHex(storedHex);
        return MessageDigest.isEqual(sha256Bytes(fileBytes), storedBytes);
    }
}
