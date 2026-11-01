package com.abhapharmacy.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {

    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    /**
     * Hashes raw passwords using salted BCrypt for secure storage in MySQL.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        return BCRYPT_ENCODER.encode(password);
    }

    /**
     * Verifies a raw password against the stored database hash.
     * Supports both modern BCrypt ($2a$, $2b$, $2y$) and legacy SHA-256 (pre-seeded in data.sql)
     * to guarantee backward compatibility without breaking existing sample accounts.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return BCRYPT_ENCODER.matches(rawPassword, storedHash);
        }
        // Fallback for legacy SHA-256 hashes
        return storedHash.equalsIgnoreCase(hashSha256(rawPassword));
    }

    /**
     * Legacy SHA-256 calculation for backward compatibility with data.sql.
     */
    public static String hashSha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
