package com.smarthome.devices;

/**
 * Security device implementation
 */
public class SecurityDevice extends Device {
    public enum Type {
        CAMERA, MOTION_SENSOR, ALARM
    }

    private Type deviceType;
    private boolean isArmed;

    public SecurityDevice(String id, String name, Type deviceType) {
        super(id, name);
        this.deviceType = deviceType;
        this.isArmed = false;
    }

    public Type getSecurityType() {
        return deviceType;
    }

    public boolean isArmed() {
        return isArmed;
    }

    public void arm() {
        this.isArmed = true;
        System.out.println(getName() + " is now armed");
    }

    public void disarm() {
        this.isArmed = false;
        System.out.println(getName() + " is now disarmed");
    }

    public void triggerAlarm() {
        if (isOn() && isArmed) {
            System.out.println("ALERT: " + getName() + " has been triggered!");
        }
    }

    @Override
    public String getDeviceType() {
        return "Security (" + deviceType + ")";
    }
}
