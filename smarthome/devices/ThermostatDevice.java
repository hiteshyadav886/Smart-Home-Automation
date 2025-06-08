package com.smarthome.devices;

/**
 * Thermostat device implementation
 */
public class ThermostatDevice extends Device implements EnergyMonitored {
    private double temperature;
    private double targetTemperature;
    private double energyUsed;
    private long lastStatusChangeTime;

    public ThermostatDevice(String id, String name, double defaultTemperature) {
        super(id, name);
        this.temperature = defaultTemperature;
        this.targetTemperature = defaultTemperature;
        this.energyUsed = 0.0;
        this.lastStatusChangeTime = System.currentTimeMillis();
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.targetTemperature = temperature;
        System.out.println(getName() + " target temperature set to " + temperature + "°C");
    }

    // Simulate temperature change
    public void updateCurrentTemperature(double newTemperature) {
        this.temperature = newTemperature;
        System.out.println(getName() + " current temperature updated to " + temperature + "°C");
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
            // Energy usage depends on the difference between current and target temperature
            double tempDiff = Math.abs(temperature - targetTemperature);
            energyUsed += (0.5 * tempDiff * hoursElapsed); // Simplified energy model
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
        return "Thermostat";
    }
}
