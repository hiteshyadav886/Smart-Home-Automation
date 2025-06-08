package com.smarthome.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages permissions for devices and users
 */
public class PermissionManager implements Serializable {
    private Map<String, Set<String>> userPermissions;
    private Map<String, Set<String>> devicePermissions;

    /**
     * Creates a new permission manager
     */
    public PermissionManager() {
        this.userPermissions = new HashMap<>();
        this.devicePermissions = new HashMap<>();
    }

    /**
     * Grants a permission to a user
     * @param username The username
     * @param permission The permission to grant
     */
    public void grantUserPermission(String username, String permission) {
        userPermissions.computeIfAbsent(username, k -> new HashSet<>()).add(permission);
    }

    /**
     * Revokes a permission from a user
     * @param username The username
     * @param permission The permission to revoke
     */
    public void revokeUserPermission(String username, String permission) {
        if (userPermissions.containsKey(username)) {
            userPermissions.get(username).remove(permission);
        }
    }

    /**
     * Checks if a user has a specific permission
     * @param username The username
     * @param permission The permission to check
     * @return true if the user has the permission
     */
    public boolean hasUserPermission(String username, String permission) {
        return userPermissions.containsKey(username) &&
                userPermissions.get(username).contains(permission);
    }

    /**
     * Sets permissions for a device
     * @param deviceId The device ID
     * @param permissions Set of permissions required to access the device
     */
    public void setDevicePermissions(String deviceId, Set<String> permissions) {
        devicePermissions.put(deviceId, new HashSet<>(permissions));
    }

    /**
     * Checks if a user can access a device
     * @param username The username
     * @param deviceId The device ID
     * @return true if the user can access the device
     */
    public boolean canAccessDevice(String username, String deviceId) {
        if (!devicePermissions.containsKey(deviceId)) {
            return true; // No specific permissions required
        }

        if (!userPermissions.containsKey(username)) {
            return false; // User has no permissions
        }

        Set<String> requiredPermissions = devicePermissions.get(deviceId);
        Set<String> userPerms = userPermissions.get(username);

        // Check if user has at least one of the required permissions
        for (String permission : requiredPermissions) {
            if (userPerms.contains(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets all permissions for a user
     * @param username The username
     * @return Set of permissions for the user
     */
    public Set<String> getUserPermissions(String username) {
        return userPermissions.getOrDefault(username, new HashSet<>());
    }
}

