package net.runelite.client.plugins.microbot.prayerhelper;

import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;

@ConfigGroup("prayerhelper")
public interface PrayerHelperConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "meleePrayerKey",
            name = "Protect from Melee Key",
            description = "The key that will toggle Protect from Melee."
    )
    default ModifierlessKeybind meleePrayerKey()
    {
        return new ModifierlessKeybind(KeyEvent.VK_1, 0); // Set default to '1'
    }

    @ConfigItem(
            position = 1,
            keyName = "magicPrayerKey",
            name = "Protect from Magic Key",
            description = "The key that will toggle Protect from Magic."
    )
    default ModifierlessKeybind magicPrayerKey()
    {
        return new ModifierlessKeybind(KeyEvent.VK_2, 0); // Set default to '2'
    }

    @ConfigItem(
            position = 2,
            keyName = "rangePrayerKey",
            name = "Protect from Range Key",
            description = "The key that will toggle Protect from Range."
    )
    default ModifierlessKeybind rangePrayerKey()
    {
        return new ModifierlessKeybind(KeyEvent.VK_3, 0); // Set default to '3'
    }
}
