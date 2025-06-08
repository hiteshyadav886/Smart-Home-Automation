package com.smarthome;

import com.smarthome.devices.*;
import com.smarthome.users.*;
import com.smarthome.automation.*;
import com.smarthome.security.SecurityManager;  // Updated import
import com.smarthome.utils.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main class that manages the entire Smart Home Automation System
 */
public class SmartHomeSystem {
    private List<Device> devices;
    private List<User> users;
    private List<AutomationRule> rules;
    private SecurityManager securityManager;  // Updated type
    private Logger logger;
    private boolean systemRunning;
    private static final String CONFIG_FILE = "system_config.dat";
    private static final String LOG_FILE = "system.log";

    // Default constructor
    public SmartHomeSystem() {
        this.devices = new ArrayList<>();
        this.users = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.securityManager = new SecurityManager();  // Updated constructor
        this.logger = new Logger(LOG_FILE, Logger.LogLevel.INFO);
        this.systemRunning = false;

        // Create default admin user
        users.add(new AdminUser("admin", "admin123", "System Administrator"));

        logger.info("Smart Home System initialized");
    }

    // Overloaded constructor with configuration file
    public SmartHomeSystem(String configFile) {
        this();
        loadConfiguration(configFile);
    }

    // Start the system
    public void start() {
        systemRunning = true;

        // Start device monitoring thread
        Thread monitoringThread = new Thread(new DeviceMonitor(devices));
        monitoringThread.setDaemon(true);
        monitoringThread.start();

        logger.info("Smart Home System started successfully!");
    }

    // Stop the system
    public void stop() {
        systemRunning = false;
        saveConfiguration(CONFIG_FILE);
        logger.info("Smart Home System stopped. Configuration saved.");
    }

    // Add a device to the system
    public void addDevice(Device device) {
        devices.add(device);
        logger.info("Device added: " + device.getName());
    }

    // Overloaded method to add multiple devices at once (varargs)
    public void addDevice(Device... newDevices) {
        for (Device device : newDevices) {
            devices.add(device);
            logger.info("Device added: " + device.getName());
        }
    }

    // Add a user to the system
    public void addUser(User user) {
        users.add(user);
        logger.info("User added: " + user.getUsername());
    }

    // Overloaded method to add multiple users at once (varargs)
    public void addUser(User... newUsers) {
        for (User user : newUsers) {
            users.add(user);
            logger.info("User added: " + user.getUsername());
        }
    }

    // Add an automation rule
    public void addRule(AutomationRule rule) {
        rules.add(rule);
        logger.info("Automation rule added: " + rule.getName());
    }

    // Get device by ID
    public Device getDeviceById(String id) {
        for (Device device : devices) {
            if (device.getId().equals(id)) {
                return device;
            }
        }
        return null;
    }

    // Get all devices
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }

    // Get all rules
    public List<AutomationRule> getRules() {
        return new ArrayList<>(rules);
    }

    // Authenticate user
    public User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.authenticate(password)) {
                logger.info("User authenticated: " + username);
                return user;
            }
        }
        logger.warning("Failed authentication attempt for user: " + username);
        return null;
    }

    // Save system configuration to file
    private void saveConfiguration(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(devices);
            out.writeObject(users);
            out.writeObject(rules);
            logger.info("Configuration saved to " + filename);
        } catch (IOException e) {
            logger.error("Error saving configuration: " + e.getMessage());
        }
    }

    // Load system configuration from file
    @SuppressWarnings("unchecked")
    private void loadConfiguration(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            devices = (List<Device>) in.readObject();
            users = (List<User>) in.readObject();
            rules = (List<AutomationRule>) in.readObject();
            logger.info("Configuration loaded from " + filename);
        } catch (FileNotFoundException e) {
            logger.info("No existing configuration found. Starting with defaults.");
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error loading configuration: " + e.getMessage());
        }
    }

    // Inner class for device monitoring (implements Runnable for multithreading)
    private class DeviceMonitor implements Runnable {
        private List<Device> monitoredDevices;

        public DeviceMonitor(List<Device> devices) {
            this.monitoredDevices = devices;
        }

        @Override
        public void run() {
            while (systemRunning) {
                for (Device device : monitoredDevices) {
                    // Check device status and apply automation rules
                    for (AutomationRule rule : rules) {
                        try {
                            if (rule.shouldTrigger(device)) {
                                rule.execute();
                            }
                        } catch (Exception e) {
                            logger.error("Error executing rule " + rule.getName() + ": " + e.getMessage());
                        }
                    }

                    // Log energy consumption
                    if (device instanceof EnergyMonitored) {
                        double consumption = ((EnergyMonitored) device).getEnergyConsumption();
                        logger.debug("Energy consumption for " + device.getName() + ": " + consumption + " kWh");
                    }
                }

                // Sleep for 5 seconds before next check
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warning("Device monitoring thread interrupted");
                    return;
                }
            }
        }
    }

    // Main method to run the system
    public static void main(String[] args) {
        SmartHomeSystem system = new SmartHomeSystem();

        // Add some sample devices
        system.addDevice(
                new LightDevice("L001", "Living Room Light"),
                new ThermostatDevice("T001", "Living Room AC", 24.0),
                new SecurityDevice("S001", "Front Door Camera", SecurityDevice.Type.CAMERA)
        );

        // Add some automation rules
        system.addRule(new TimeBasedRule("Morning Lights", "07:00",
                () -> {
                    Device device = system.getDeviceById("L001");
                    if (device != null) {
                        device.turnOn();
                    }
                }));

        system.addRule(new EventBasedRule("Motion Detection", "MOTION_DETECTED",
                () -> {
                    Device light = system.getDeviceById("L001");
                    Device camera = system.getDeviceById("S001");
                    if (light != null) light.turnOn();
                    if (camera != null) camera.turnOn();
                }));

        // Start the system
        system.start();

        // Create a simple command-line interface
        Scanner scanner = new Scanner(System.in);
        User currentUser = null;

        System.out.println("Welcome to Smart Home Automation System");
        System.out.println("Type 'help' for available commands");

        while (true) {
            try {
                if (currentUser == null) {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    if (username.equalsIgnoreCase("exit")) {
                        break;
                    }

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    currentUser = system.authenticateUser(username, password);
                    if (currentUser == null) {
                        System.out.println("Invalid credentials. Please try again.");
                    } else {
                        System.out.println("Welcome, " + currentUser.getName() + "!");
                    }
                } else {
                    System.out.print(currentUser.getUsername() + "> ");
                    String command = scanner.nextLine();

                    if (command.equalsIgnoreCase("help")) {
                        System.out.println("Available commands:");
                        System.out.println("  devices - List all devices");
                        System.out.println("  control <deviceId> - Control a device");
                        System.out.println("  rules - List all automation rules");
                        System.out.println("  logout - Log out");
                        System.out.println("  exit - Exit the system");
                    } else if (command.equalsIgnoreCase("devices")) {
                        System.out.println("Available devices:");
                        for (Device device : system.getDevices()) {
                            System.out.println("  " + device.getId() + ": " + device.getName() +
                                    " - " + (device.isOn() ? "ON" : "OFF"));
                        }
                    } else if (command.startsWith("control ")) {
                        String deviceId = command.substring(8).trim();
                        Device device = system.getDeviceById(deviceId);

                        if (device == null) {
                            System.out.println("Device not found: " + deviceId);
                        } else {
                            controlDevice(scanner, device);
                        }
                    } else if (command.equalsIgnoreCase("rules")) {
                        System.out.println("Automation rules:");
                        for (AutomationRule rule : system.getRules()) {
                            System.out.println("  " + rule.getName());
                        }
                    } else if (command.equalsIgnoreCase("logout")) {
                        currentUser = null;
                        System.out.println("Logged out successfully");
                    } else if (command.equalsIgnoreCase("exit")) {
                        break;
                    } else {
                        System.out.println("Unknown command. Type 'help' for available commands.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // Stop the system and close resources
        system.stop();
        scanner.close();
        System.out.println("System shutdown complete. Goodbye!");
    }

    // Helper method to control a device
    private static void controlDevice(Scanner scanner, Device device) {
        System.out.println("Device: " + device.getName());
        System.out.println("Status: " + (device.isOn() ? "ON" : "OFF"));
        System.out.println("Commands:");
        System.out.println("  on - Turn device on");
        System.out.println("  off - Turn device off");

        if (device instanceof LightDevice) {
            System.out.println("  brightness <0-100> - Set brightness");
        } else if (device instanceof ThermostatDevice) {
            System.out.println("  temp <value> - Set temperature");
        } else if (device instanceof SecurityDevice) {
            System.out.println("  arm - Arm the device");
            System.out.println("  disarm - Disarm the device");
        }

        System.out.println("  back - Return to main menu");

        while (true) {
            System.out.print(device.getId() + "> ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("on")) {
                device.turnOn();
                System.out.println("Device turned ON");
            } else if (command.equalsIgnoreCase("off")) {
                device.turnOff();
                System.out.println("Device turned OFF");
            } else if (command.startsWith("brightness ") && device instanceof LightDevice) {
                try {
                    int brightness = Integer.parseInt(command.substring(11).trim());
                    ((LightDevice) device).setBrightness(brightness);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid brightness value");
                }
            } else if (command.startsWith("temp ") && device instanceof ThermostatDevice) {
                try {
                    double temp = Double.parseDouble(command.substring(5).trim());
                    ((ThermostatDevice) device).setTemperature(temp);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid temperature value");
                }
            } else if (command.equalsIgnoreCase("arm") && device instanceof SecurityDevice) {
                ((SecurityDevice) device).arm();
                System.out.println("Device armed");
            } else if (command.equalsIgnoreCase("disarm") && device instanceof SecurityDevice) {
                ((SecurityDevice) device).disarm();
                System.out.println("Device disarmed");
            } else if (command.equalsIgnoreCase("back")) {
                return;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
}
