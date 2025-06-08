package com.smarthome.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages security aspects of the smart home system
 */
public class SecurityManager implements Serializable {
    private Map<String, String> accessTokens;
    private boolean secureMode;

    public SecurityManager() {
        this.accessTokens = new HashMap<>();
        this.secureMode = true;
    }

    /**
     * Generates an access token for a user
     * @param username The username to generate token for
     * @return The generated token
     */
    public String generateAccessToken(String username) {
        // In a real system, this would generate a secure token
        String token = "TOKEN_" + username + "_" + System.currentTimeMillis();
        accessTokens.put(username, token);
        return token;
    }

    /**
     * Validates a user's access token
     * @param username The username
     * @param token The token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String username, String token) {
        return token.equals(accessTokens.get(username));
    }

    /**
     * Revokes a user's access token
     * @param username The username whose token to revoke
     */
    public void revokeToken(String username) {
        accessTokens.remove(username);
    }

    /**
     * Enables secure mode for the system
     */
    public void enableSecureMode() {
        secureMode = true;
        System.out.println("Secure mode enabled");
    }

    /**
     * Disables secure mode for the system
     */
    public void disableSecureMode() {
        secureMode = false;
        System.out.println("Secure mode disabled");
    }

    /**
     * Checks if secure mode is enabled
     * @return true if secure mode is enabled
     */
    public boolean isSecureModeEnabled() {
        return secureMode;
    }

    /**
     * Static nested class for encryption operations
     */
    public static class Encryptor {
        /**
         * Encrypts data (simplified implementation)
         * @param data The data to encrypt
         * @return The encrypted data
         */
        public static String encrypt(String data) {
            // Simple encryption for demonstration (not secure)
            StringBuilder encrypted = new StringBuilder();
            for (char c : data.toCharArray()) {
                encrypted.append((char)(c + 1));
            }
            return encrypted.toString();
        }

        /**
         * Decrypts data (simplified implementation)
         * @param data The data to decrypt
         * @return The decrypted data
         */
        public static String decrypt(String data) {
            // Simple decryption for demonstration
            StringBuilder decrypted = new StringBuilder();
            for (char c : data.toCharArray()) {
                decrypted.append((char)(c - 1));
            }
            return decrypted.toString();
        }
    }
}
