package com.smarthome.security;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logs security-related events to a file
 */
public class SecurityLogger implements Serializable {
    private String logFilePath;
    private boolean loggingEnabled;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new security logger
     * @param logFilePath Path to the log file
     */
    public SecurityLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        this.loggingEnabled = true;

        // Create log directory if it doesn't exist
        File logFile = new File(logFilePath);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    /**
     * Enables or disables logging
     * @param enabled true to enable logging, false to disable
     */
    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
    }

    /**
     * Logs a security event
     * @param event The event description
     * @param level The severity level (INFO, WARNING, ERROR)
     */
    public void logEvent(String event, LogLevel level) {
        if (!loggingEnabled) {
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = timestamp + " [" + level + "] " + event;

        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(logEntry + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to security log: " + e.getMessage());
        }
    }

    /**
     * Logs an authentication attempt
     * @param username The username
     * @param success Whether authentication was successful
     * @param ipAddress The IP address of the client (if available)
     */
    public void logAuthenticationAttempt(String username, boolean success, String ipAddress) {
        String event = "Authentication attempt for user '" + username + "' from " +
                (ipAddress != null ? ipAddress : "unknown location") +
                " - " + (success ? "SUCCESS" : "FAILURE");

        logEvent(event, success ? LogLevel.INFO : LogLevel.WARNING);
    }

    /**
     * Logs device access
     * @param username The username
     * @param deviceId The device ID
     * @param action The action performed
     */
    public void logDeviceAccess(String username, String deviceId, String action) {
        String event = "User '" + username + "' performed action '" + action +
                "' on device '" + deviceId + "'";

        logEvent(event, LogLevel.INFO);
    }

    /**
     * Log levels for security events
     */
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }
}

