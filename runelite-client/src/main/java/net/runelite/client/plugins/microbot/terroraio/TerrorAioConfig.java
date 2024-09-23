package net.runelite.client.plugins.microbot.terroraio;

import net.runelite.client.config.*;

@ConfigGroup("terrorAio")
public interface TerrorAioConfig extends Config {

    @ConfigItem(
            keyName = "toggleEat",
            name = "Enable Eating",
            description = "Toggle the automatic eating feature."
    )
    default boolean toggleEat() {
        return true; // Default to true
    }

    @ConfigItem(
            keyName = "healthThreshold",
            name = "Health Threshold",
            description = "Health percentage at which to eat."
    )
    @Range(min = 0, max = 100)
    default int healthThreshold() {
        return 50; // Default to 50%
    }

    @ConfigItem(
            keyName = "allowedFood",
            name = "Allowed Foods",
            description = "Comma-separated list of allowed food items."
    )
    default String allowedFood() {
        return "Shark, Saradomin brew"; // Default allowed foods
    }

    @ConfigItem(
            keyName = "togglePrayer",
            name = "Enable Prayer Restoration",
            description = "Toggle the automatic prayer restoration feature."
    )
    default boolean togglePrayer() {
        return true; // Default to true
    }

    @ConfigItem(
            keyName = "toggleSpec",
            name = "Enable Special Attack",
            description = "Toggle the automatic special attack feature."
    )
    default boolean toggleSpec() {
        return true; // Default to true
    }
}
