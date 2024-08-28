package net.runelite.client.plugins.microbot.piebaker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = PluginDescriptor.Bttqjs + "Pie Baker",
        description = "Automatically bakes pies using the Lunar spellbook",
        tags = {"pie", "baking", "cooking", "magic"},
        enabledByDefault = false
)
@Slf4j
public class PieBakerPlugin extends Plugin {

    @Inject
    private PieBakerConfig config;

    private static final Map<String, Integer> RAW_PIES = new HashMap<>();
    private static final String WATER_RUNE_NAME = "Water rune";
    private static final String FIRE_RUNE_NAME = "Fire rune";
    private static final String ASTRAL_RUNE_NAME = "Astral rune";

    private Instant lastBakeTime = Instant.now();

    static {
        RAW_PIES.put("uncooked berry pie", ItemID.UNCOOKED_BERRY_PIE);
        RAW_PIES.put("uncooked meat pie", ItemID.UNCOOKED_MEAT_PIE);
        RAW_PIES.put("raw mud pie", ItemID.RAW_MUD_PIE);
        RAW_PIES.put("uncooked apple pie", ItemID.UNCOOKED_APPLE_PIE);
        RAW_PIES.put("raw garden pie", ItemID.RAW_GARDEN_PIE);
        RAW_PIES.put("raw fish pie", ItemID.RAW_FISH_PIE);
        RAW_PIES.put("uncooked botanical pie", ItemID.UNCOOKED_BOTANICAL_PIE);
        RAW_PIES.put("uncooked mushroom pie", ItemID.UNCOOKED_MUSHROOM_PIE);
        RAW_PIES.put("raw admiral pie", ItemID.RAW_ADMIRAL_PIE);
        RAW_PIES.put("uncooked dragonfruit pie", ItemID.UNCOOKED_DRAGONFRUIT_PIE);
        RAW_PIES.put("raw wild pie", ItemID.RAW_WILD_PIE);
        RAW_PIES.put("raw summer pie", ItemID.RAW_SUMMER_PIE);
    }

    private Instant startTime;
    private int startMagicXP;
    private int startCookingXP;

    @Provides
    PieBakerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PieBakerConfig.class);
    }

    @Override
    protected void startUp() {
        startTime = Instant.now();
        startMagicXP = Microbot.getClient().getSkillExperience(Skill.MAGIC);
        startCookingXP = Microbot.getClient().getSkillExperience(Skill.COOKING);
    }

    @Override
    protected void shutDown() {
        // No additional cleanup needed.
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.BANK.getId()) {
            if (!Rs2Inventory.hasItem(RAW_PIES.keySet().toArray(new String[0])) && shouldWithdrawRunes()) {
                withdrawRunes();
            }
        }
    }


    @Subscribe
    public void onGameTick(GameTick event) {
        String[] rawPiesArray = RAW_PIES.keySet().toArray(new String[0]);

        if (Rs2Inventory.hasItem(rawPiesArray)) {
            if (Instant.now().isAfter(lastBakeTime.plusSeconds(5))) {
                if (Rs2Bank.isOpen()) {
                    Rs2Bank.closeBank();
                } else {
                    castBakePieSpell();
                    lastBakeTime = Instant.now();  // Update last bake time after casting the spell
                }
            }
        } else {
            if (!Rs2Bank.isOpen()) {
                if (Rs2Bank.walkToBank()) {  // Walk to the nearest bank if not already there
                    Rs2Bank.openBank();  // Attempt to open the bank once the player is there
                }
            } else {
                // Deposit all items and withdraw new ones in a single step
                depositAndWithdraw(rawPiesArray);
            }
        }
    }

    private void depositAndWithdraw(String[] rawPiesArray) {
        // Ensure the bank is open before proceeding
        Rs2Bank.depositAll(); // Deposit all items in the inventory

        if (areRunesWithdrawn()) {
            withdrawPies();
        } else {
            withdrawRunes(); // Retry withdrawing runes if not done
        }
    }


    private boolean shouldWithdrawRunes() {
        return Rs2Bank.hasItem(WATER_RUNE_NAME) || Rs2Bank.hasItem(FIRE_RUNE_NAME) || Rs2Bank.hasItem(ASTRAL_RUNE_NAME);
    }

    private void withdrawRunes() {
        if (Rs2Bank.isOpen()) {
            Rs2Bank.withdrawAll(WATER_RUNE_NAME); // Withdraw all water runes
            Rs2Bank.withdrawAll(FIRE_RUNE_NAME);  // Withdraw all fire runes
            Rs2Bank.withdrawAll(ASTRAL_RUNE_NAME); // Withdraw all astral runes
        }
    }

    private boolean areRunesWithdrawn() {
        return Rs2Inventory.hasItem(WATER_RUNE_NAME) && Rs2Inventory.hasItem(FIRE_RUNE_NAME) && Rs2Inventory.hasItem(ASTRAL_RUNE_NAME);
    }

    private void withdrawPies() {
        if (Rs2Bank.isOpen()) {
            Rs2Bank.withdrawAll(getSelectedRawPieId()); // Withdraw raw pies
        }
    }

    private void castBakePieSpell() {
        String[] rawPiesArray = RAW_PIES.keySet().toArray(new String[0]);

        if (config.tickPerfect()) {
            for (int i = 0; i < 10 && Rs2Inventory.hasItem(rawPiesArray); i++) {
                Rs2Magic.cast(MagicAction.BAKE_PIE);
            }
        } else {
            Rs2Magic.cast(MagicAction.BAKE_PIE);
        }
    }


    private int getSelectedRawPieId() {
        return RAW_PIES.getOrDefault(config.selectedPie().toLowerCase(), -1);
    }

    public String getSelectedPie() {
        return config.selectedPie();
    }

    public int getMagicXPPerHour() {
        int currentXP = Microbot.getClient().getSkillExperience(Skill.MAGIC);
        return calculateXPPerHour(startMagicXP, currentXP);
    }

    public int getCookingXPPerHour() {
        int currentXP = Microbot.getClient().getSkillExperience(Skill.COOKING);
        return calculateXPPerHour(startCookingXP, currentXP);
    }

    private int calculateXPPerHour(int startXP, int currentXP) {
        long elapsedTime = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        int xpGained = currentXP - startXP;
        return (int) (xpGained * 3600 / elapsedTime);
    }
}
