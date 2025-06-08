package com.smarthome.automation;

import java.io.Serializable;

/**
 * Interface for all automation rules in the system
 */
public interface AutomationRule extends Serializable {
    /**
     * Get the name of this automation rule
     * @return The rule name
     */
    String getName();

    /**
     * Check if this rule should be triggered based on the given context
     * @param context The context object to check against (usually a device)
     * @return true if the rule should trigger
     */
    boolean shouldTrigger(Object context);

    /**
     * Execute the actions associated with this rule
     */
    void execute();
}
