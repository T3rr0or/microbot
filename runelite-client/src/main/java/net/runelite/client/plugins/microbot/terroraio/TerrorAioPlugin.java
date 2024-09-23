package net.runelite.client.plugins.microbot.terroraio;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = PluginDescriptor.xsvl + "Player Support",
        description = "Aio tool that does stuff for you.",
        tags = {"auto", "tools", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class TerrorAioPlugin extends Plugin {
    @Inject
    private TerrorAioConfig config;

    @Provides
    TerrorAioConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TerrorAioConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        log.info("Player support Plugin started.");
    }

    @Override
    protected void shutDown() {
        log.info("Player support Plugin stopped.");
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        // Get the allowed foods from the config
        String allowedFoodConfig = config.allowedFood();
        List<String> allowedFoodList = Arrays.asList(allowedFoodConfig.split(",\\s*"));

        // Check toggles for eating, prayer, and spec
        if (config.toggleEat()) {
            Rs2Player.eatAt(config.healthThreshold(), allowedFoodList);
        }

        if (config.togglePrayer()) {
            Rs2Player.drinkPrayerPotionAt(20);
            Rs2Player.drinkRestorePotionAt(20);
        }

        if (config.toggleSpec()) {
            Rs2Combat.setSpecState(true, 500);
        }
    }
}
