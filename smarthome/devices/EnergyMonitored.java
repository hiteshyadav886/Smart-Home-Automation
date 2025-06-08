package com.smarthome.devices;

/**
 * Interface for devices that can monitor energy consumption
 */
public interface EnergyMonitored {
    double getEnergyConsumption();
    void resetEnergyStats();
}

