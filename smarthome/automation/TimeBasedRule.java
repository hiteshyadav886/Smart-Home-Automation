package com.smarthome.automation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Rule that triggers at specific times of day
 */
public class TimeBasedRule implements AutomationRule {
    private String name;
    private LocalTime triggerTime;
    private Runnable action;
    private boolean executed;

    /**
     * Create a new time-based rule
     * @param name Rule name
     * @param time Time to trigger in HH:mm format (24-hour)
     * @param action Action to execute when triggered
     */
    public TimeBasedRule(String name, String time, Runnable action) {
        this.name = name;
        this.triggerTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        this.action = action;
        this.executed = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean shouldTrigger(Object context) {
        LocalTime now = LocalTime.now();

        // Check if current time matches trigger time (within a minute)
        boolean timeMatches = Math.abs(now.getHour() - triggerTime.getHour()) == 0 &&
                Math.abs(now.getMinute() - triggerTime.getMinute()) == 0;

        // Only trigger once per minute
        if (timeMatches && !executed) {
            executed = true;
            return true;
        } else if (!timeMatches) {
            executed = false;
        }

        return false;
    }

    @Override
    public void execute() {
        System.out.println("Executing time-based rule: " + name);
        action.run();
    }
}
