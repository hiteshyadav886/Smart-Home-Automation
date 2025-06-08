package com.smarthome.devices;

/**
 * Light device implementation
 */
public class LightDevice extends Device implements EnergyMonitored {
    private int brightness;
    private double energyUsed;
    private long lastStatusChangeTime;

    // Default constructor
    public LightDevice(String id, String name) {
        super(id, name);
        this.brightness = 100;
        this.energyUsed = 0.0;
        this.lastStatusChangeTime = System.currentTimeMillis();
    }

    // Overloaded constructor with brightness
    public LightDevice(String id, String name, int brightness) {
        super(id, name);
        this.brightness = brightness;
        this.energyUsed = 0.0;
        this.lastStatusChangeTime = System.currentTimeMillis();
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness < 0) {
            this.brightness = 0;
        } else if (brightness > 100) {
            this.brightness = 100;
        } else {
            this.brightness = brightness;
        }
        System.out.println(getName() + " brightness set to " + this.brightness + "%");
    }

    @Override
    public void turnOn() {
        updateEnergyUsage();
        super.turnOn();
        lastStatusChangeTime = System.currentTimeMillis();
    }

    @Override
    public void turnOff() {
        updateEnergyUsage();
        super.turnOff();
        lastStatusChangeTime = System.currentTimeMillis();
    }

    private void updateEnergyUsage() {
        if (isOn()) {
            // Calculate energy used since last status change (simplified model)
            long currentTime = System.currentTimeMillis();
            double hoursElapsed = (currentTime - lastStatusChangeTime) / 3600000.0;
            // Assume 10W per hour at 100% brightness
            energyUsed += (0.01 * brightness * hoursElapsed);
        }
    }

    @Override
    public double getEnergyConsumption() {
        updateEnergyUsage();
        return energyUsed;
    }

    @Override
    public void resetEnergyStats() {
        energyUsed = 0.0;
        lastStatusChangeTime = System.currentTimeMillis();
    }

    @Override
    public String getDeviceType() {
        return "Light";
    }
}
