package com.smarthome.users;

import java.io.Serializable;

/**
 * Abstract base class for all users in the system
 */
public abstract class User implements Serializable {
    private String username;
    private String passwordHash; // Simplified for demo (use proper hashing in real systems)
    private String name;

    public User(String username, String password, String name) {
        this.username = username;
        this.passwordHash = password; // In real systems, use BCrypt or similar
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean authenticate(String password) {
        return passwordHash.equals(password); // Replace with secure comparison
    }

    /**
     * Abstract method to check user permissions
     * @param permission The permission to check
     * @return true if user has permission
     */
    public abstract boolean hasPermission(String permission);
}
