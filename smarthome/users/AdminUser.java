package com.smarthome.users;

/**
 * Admin user with full system permissions
 */
public class AdminUser extends User {

    public AdminUser(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public boolean hasPermission(String permission) {
        // Admins have all permissions
        return true;
    }
}
