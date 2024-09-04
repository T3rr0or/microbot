package net.runelite.client.plugins.microbot.flaxpicker;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

@PluginDescriptor(
        name = PluginDescriptor.xsvl + "Flax Picker",
        description = "Picks up flax until the inventory is full, then banks it.",
        tags = {"flax", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class FlaxPickerPlugin extends Plugin {

    private final WorldPoint flaxLocation = new WorldPoint(2741, 3444, 0); // Example coordinates, adjust as needed
    private final WorldPoint bankLocation = new WorldPoint(2724, 3491, 0); // Example coordinates, adjust as needed

    @Subscribe
    public void onGameTick(GameTick event) {
        // If inventory is full, walk to the bank and deposit flax
        if (Rs2Inventory.isFull()) {
            if (Rs2Player.getWorldLocation().distanceTo(bankLocation) > 5) {
                Rs2Walker.walkTo(bankLocation);
            } else {
                if (!Rs2Bank.isOpen()) {  // Check if the bank is already open
                    TileObject bank = Rs2GameObject.findBank();  // Find the bank object
                    if (bank != null) {
                        Rs2GameObject.interact(bank.getId(), "Bank", 10);  // Interact with the bank to open it
                    }
                } else {
                    Rs2Bank.depositAll("Flax");  // Deposit all flax if the bank is open
                }
            }
        } else {
            // If inventory is not full, walk to flax location and pick flax
            if (Rs2Player.getWorldLocation().distanceTo(flaxLocation) > 5) {
                Rs2Walker.walkTo(flaxLocation);
            } else {
                TileObject flax = Rs2GameObject.findObjectById(14896);
                if (flax != null) {
                    Rs2GameObject.interact(14896, "Pick", 5);
                }
            }
        }
    }
}
