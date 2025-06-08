package com.smarthome.automation;

import com.smarthome.devices.Device;

/**
 * Rule that triggers based on specific events
 */
public class EventBasedRule implements AutomationRule {
    private String name;
    private String triggerEvent;
    private Runnable action;

    /**
     * Create a new event-based rule
     * @param name Rule name
     * @param triggerEvent Event that triggers this rule
     * @param action Action to execute when triggered
     */
    public EventBasedRule(String name, String triggerEvent, Runnable action) {
        this.name = name;
        this.triggerEvent = triggerEvent;
        this.action = action;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean shouldTrigger(Object context) {
        // In a real system, this would check if the context matches the trigger event
        // For simulation purposes, we'll randomly trigger events
        if (context instanceof Device) {
            // Simulate random events for demo purposes (1% chance)
            if (Math.random() < 0.01) {
                System.out.println("Event detected: " + triggerEvent + " on " + ((Device)context).getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute() {
        System.out.println("Executing event-based rule: " + name);
        action.run();
    }
}
