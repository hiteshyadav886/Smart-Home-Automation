package com.smarthome.ui;

import com.smarthome.SmartHomeSystem;
import com.smarthome.devices.*;
import com.smarthome.users.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Graphical User Interface for the Smart Home System
 */
public class SmartHomeGUI extends JFrame {
    private SmartHomeSystem system;
    private User currentUser;

    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel devicePanel;

    /**
     * Creates a new GUI for the Smart Home System
     * @param system The Smart Home System instance
     */
    public SmartHomeGUI(SmartHomeSystem system) {
        this.system = system;
        this.currentUser = null;

        setTitle("Smart Home Automation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize panels
        mainPanel = new JPanel(new CardLayout());
        loginPanel = createLoginPanel();
        devicePanel = new JPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(devicePanel, "DEVICES");

        // Show login panel first
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "LOGIN");

        add(mainPanel);

        // Add window listener to save configuration on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                system.stop();
            }
        });
    }

    /**
     * Creates the login panel
     * @return The login panel
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Smart Home Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // Add login button action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = system.authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                updateDevicePanel();
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "DEVICES");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Updates the device panel with current devices
     */
    private void updateDevicePanel() {
        devicePanel.removeAll();
        devicePanel.setLayout(new BorderLayout());

        // Create top panel with user info and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            currentUser = null;
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "LOGIN");
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Create device list panel
        JPanel devicesListPanel = new JPanel();
        devicesListPanel.setLayout(new BoxLayout(devicesListPanel, BoxLayout.Y_AXIS));

        // Add some demo devices
        List<Device> devices = new ArrayList<>();
        devices.add(new LightDevice("L001", "Living Room Light"));
        devices.add(new ThermostatDevice("T001", "Living Room AC", 24.0));
        devices.add(new SecurityDevice("S001", "Front Door Camera", SecurityDevice.Type.CAMERA));

        for (Device device : devices) {
            JPanel deviceControl = createDeviceControlPanel(device);
            devicesListPanel.add(deviceControl);
        }

        JScrollPane scrollPane = new JScrollPane(devicesListPanel);

        devicePanel.add(topPanel, BorderLayout.NORTH);
        devicePanel.add(scrollPane, BorderLayout.CENTER);

        devicePanel.revalidate();
        devicePanel.repaint();
    }

    /**
     * Creates a control panel for a device
     * @param device The device to create controls for
     * @return The device control panel
     */
    private JPanel createDeviceControlPanel(Device device) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(device.getName()));

        JLabel statusLabel = new JLabel("Status: " + (device.isOn() ? "ON" : "OFF"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton onButton = new JButton("Turn ON");
        JButton offButton = new JButton("Turn OFF");

        onButton.addActionListener(e -> {
            device.turnOn();
            statusLabel.setText("Status: ON");
        });

        offButton.addActionListener(e -> {
            device.turnOff();
            statusLabel.setText("Status: OFF");
        });

        controlPanel.add(onButton);
        controlPanel.add(offButton);

        // Add specific controls based on device type
        if (device instanceof LightDevice) {
            LightDevice light = (LightDevice) device;
            JLabel brightnessLabel = new JLabel("Brightness: " + light.getBrightness() + "%");
            JSlider brightnessSlider = new JSlider(0, 100, light.getBrightness());

            brightnessSlider.addChangeListener(e -> {
                int value = brightnessSlider.getValue();
                light.setBrightness(value);
                brightnessLabel.setText("Brightness: " + value + "%");
            });

            controlPanel.add(brightnessLabel);
            controlPanel.add(brightnessSlider);
        } else if (device instanceof ThermostatDevice) {
            ThermostatDevice thermostat = (ThermostatDevice) device;
            JLabel tempLabel = new JLabel("Temperature: " + thermostat.getTemperature() + "°C");

            JButton decreaseTemp = new JButton("-");
            JButton increaseTemp = new JButton("+");

            decreaseTemp.addActionListener(e -> {
                thermostat.setTemperature(thermostat.getTemperature() - 0.5);
                tempLabel.setText("Temperature: " + thermostat.getTemperature() + "°C");
            });

            increaseTemp.addActionListener(e -> {
                thermostat.setTemperature(thermostat.getTemperature() + 0.5);
                tempLabel.setText("Temperature: " + thermostat.getTemperature() + "°C");
            });

            controlPanel.add(tempLabel);
            controlPanel.add(decreaseTemp);
            controlPanel.add(increaseTemp);
        } else if (device instanceof SecurityDevice) {
            SecurityDevice security = (SecurityDevice) device;
            JButton armButton = new JButton("Arm");
            JButton disarmButton = new JButton("Disarm");

            armButton.addActionListener(e -> {
                security.arm();
                statusLabel.setText("Status: " + (security.isOn() ? "ON" : "OFF") +
                        " (Armed: " + security.isArmed() + ")");
            });

            disarmButton.addActionListener(e -> {
                security.disarm();
                statusLabel.setText("Status: " + (security.isOn() ? "ON" : "OFF") +
                        " (Armed: " + security.isArmed() + ")");
            });

            controlPanel.add(armButton);
            controlPanel.add(disarmButton);
        }

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Main method to run the GUI
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create and start the system
        SmartHomeSystem system = new SmartHomeSystem();
        system.start();

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            SmartHomeGUI gui = new SmartHomeGUI(system);
            gui.setVisible(true);
        });
    }
}
