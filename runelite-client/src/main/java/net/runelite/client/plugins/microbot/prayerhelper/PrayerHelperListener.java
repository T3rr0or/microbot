package net.runelite.client.plugins.microbot.prayerhelper;

import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

class PrayerHelperListener implements KeyListener
{
    @Inject
    private PrayerHelperConfig config;

    private Rs2PrayerEnum activePrayer = null; // Track the currently active prayer
    private boolean isPrayerActive = false; // Track if a prayer is currently active

    @Override
    public void keyTyped(KeyEvent e)
    {
        // Not needed for this functionality
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        // Handle toggling prayers based on keypresses for melee, magic, and range
        if (config.meleePrayerKey().matches(e)) // Melee protection key pressed
        {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
        }
        else if (config.magicPrayerKey().matches(e)) // Magic protection key pressed
        {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
        }
        else if (config.rangePrayerKey().matches(e)) // Range protection key pressed
        {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
