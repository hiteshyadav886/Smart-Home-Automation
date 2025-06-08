package com.smarthome.users;

/**
 * Regular user implementation with basic permissions
 */
public class RegularUser extends User {

    public RegularUser(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public boolean hasPermission(String permission) {
        // Regular users can only control devices and view status
        return permission.equals("DEVICE_CONTROL") ||
                permission.equals("VIEW_STATUS");
    }
}
