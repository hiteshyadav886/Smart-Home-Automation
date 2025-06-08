package com.smarthome.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger utility for the Smart Home System
 */
public class Logger {
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }

    private String logFilePath;
    private LogLevel minLevel;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Creates a new logger
     * @param logFilePath Path to the log file
     * @param minLevel Minimum log level to record
     */
    public Logger(String logFilePath, LogLevel minLevel) {
        this.logFilePath = logFilePath;
        this.minLevel = minLevel;

        // Create log directory if it doesn't exist
        File logFile = new File(logFilePath);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    /**
     * Sets the minimum log level
     * @param level The minimum log level
     */
    public void setMinLevel(LogLevel level) {
        this.minLevel = level;
    }

    /**
     * Logs a message
     * @param level Log level
     * @param message Message to log
     */
    public void log(LogLevel level, String message) {
        if (level.ordinal() < minLevel.ordinal()) {
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = timestamp + " [" + level + "] " + message;

        // Print to console
        System.out.println(logEntry);

        // Write to file
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(logEntry + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Logs a debug message
     * @param message Message to log
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /**
     * Logs an info message
     * @param message Message to log
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a warning message
     * @param message Message to log
     */
    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    /**
     * Logs an error message
     * @param message Message to log
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs an exception
     * @param e The exception to log
     */
    public void exception(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append(System.lineSeparator());

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element).append(System.lineSeparator());
        }

        log(LogLevel.ERROR, sb.toString());
    }
}
