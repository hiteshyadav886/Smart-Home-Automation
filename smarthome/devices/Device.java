package com.smarthome.devices;

import java.io.Serializable;

/**
 * Abstract base class for all devices in the smart home system
 */
public abstract class Device implements Serializable {
    private String id;
    private String name;
    private boolean isOn;

    // Constructor
    public Device(String id, String name) {
        this.id = id;
        this.name = name;
        this.isOn = false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOn() {
        return isOn;
    }

    // Device control methods
    public void turnOn() {
        isOn = true;
        System.out.println(name + " turned ON");
    }

    public void turnOff() {
        isOn = false;
        System.out.println(name + " turned OFF");
    }

    // Abstract method to be implemented by specific device types
    public abstract String getDeviceType();

    @Override
    public String toString() {
        return "Device [id=" + id + ", name=" + name + ", status=" + (isOn ? "ON" : "OFF") + "]";
    }
}

