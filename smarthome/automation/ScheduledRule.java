package com.smarthome.automation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Rule that triggers on scheduled days and times
 */
public class ScheduledRule implements AutomationRule {
    private String name;
    private LocalTime triggerTime;
    private Set<DayOfWeek> activeDays;
    private Runnable action;
    private boolean executed;

    /**
     * Create a new scheduled rule
     * @param name Rule name
     * @param time Time to trigger in HH:mm format
     * @param action Action to execute when triggered
     */
    public ScheduledRule(String name, String time, Runnable action) {
        this.name = name;
        this.triggerTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        this.action = action;
        this.executed = false;
        this.activeDays = new HashSet<>();

        // Default to all days
        for (DayOfWeek day : DayOfWeek.values()) {
            activeDays.add(day);
        }
    }

    /**
     * Set specific days when this rule should be active
     * @param days Days of week when rule should trigger
     */
    public void setActiveDays(DayOfWeek... days) {
        this.activeDays.clear();
        for (DayOfWeek day : days) {
            this.activeDays.add(day);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean shouldTrigger(Object context) {
        LocalDateTime now = LocalDateTime.now();

        // Check if current day is an active day
        if (!activeDays.contains(now.getDayOfWeek())) {
            return false;
        }

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
        System.out.println("Executing scheduled rule: " + name);
        action.run();
    }
}
