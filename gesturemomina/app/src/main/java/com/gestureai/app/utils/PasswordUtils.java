package com.gestureai.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtils {

    // Generate a different random salt for every user.
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        return bytesToHex(salt);
    }

    // Convert password + salt into a SHA-256 hash.
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            String combined = password + salt;

            byte[] hash = digest.digest(
                    combined.getBytes(StandardCharsets.UTF_8)
            );

            return bytesToHex(hash);

        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Convert binary bytes into hexadecimal text for SQLite.
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b & 0xff));
        }

        return result.toString();
    }

    // Verify the entered password against the saved hash.
    public static boolean verifyPassword(
            String password,
            String salt,
            String savedHash
    ) {
        String enteredHash = hashPassword(password, salt);

        return MessageDigest.isEqual(
                enteredHash.getBytes(StandardCharsets.UTF_8),
                savedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}