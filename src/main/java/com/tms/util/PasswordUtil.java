package com.tms.util;

import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class PasswordUtil {

    private static final String PREFIX = "scrypt$";
    private static final int COST_N = 16384;
    private static final int BLOCK_R = 8;
    private static final int PARALLEL_P = 1;
    private static final int KEY_LEN = 64; // 64 bytes = 128 hex characters
    private static final int SALT_LEN = 16; // 16 bytes = 32 hex characters

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    public static String hashPassword(String password) {
        if (password == null) {
            password = "";
        }
        byte[] saltRaw = new byte[SALT_LEN];
        RANDOM.nextBytes(saltRaw);
        String saltHex = Hex.toHexString(saltRaw);

        // Node.js crypto.scrypt passes the hex string as salt (UTF-8 bytes)
        byte[] derivedKey = SCrypt.generate(
                password.getBytes(StandardCharsets.UTF_8),
                saltHex.getBytes(StandardCharsets.UTF_8),
                COST_N,
                BLOCK_R,
                PARALLEL_P,
                KEY_LEN
        );

        return PREFIX + saltHex + "$" + Hex.toHexString(derivedKey);
    }

    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        if (isHashed(storedPassword)) {
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 3) {
                return false;
            }
            try {
                String saltHex = parts[1];
                byte[] expectedHash = Hex.decode(parts[2]);

                // Match Node.js scrypt: salt is the hex string encoded in UTF-8
                byte[] derived = SCrypt.generate(
                        rawPassword.getBytes(StandardCharsets.UTF_8),
                        saltHex.getBytes(StandardCharsets.UTF_8),
                        COST_N,
                        BLOCK_R,
                        PARALLEL_P,
                        KEY_LEN
                );

                return MessageDigest.isEqual(derived, expectedHash);
            } catch (Exception e) {
                return false;
            }
        }

        // Fallback for plain-text legacy passwords
        return MessageDigest.isEqual(
                rawPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }
}

