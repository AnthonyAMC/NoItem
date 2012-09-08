package net.worldoftomorrow.nala.ni.listeners;

import net.worldoftomorrow.nala.ni.EventTypes;
import net.worldoftomorrow.nala.ni.Perms;
import net.worldoftomorrow.nala.ni.StringHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HoldListener implements Listener {

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        int ns = event.getNewSlot();
        int ps = event.getPreviousSlot();
        ItemStack notAllowed = null;
        ItemStack allowed = null;

        if (p.getInventory().getItem(ns) != null) {
            notAllowed = p.getInventory().getItem(ns);
            allowed = p.getInventory().getItem(ps);

            // Switch the items.
            if (Perms.NOHOLD.has(p, notAllowed)) {
                p.getInventory().setItem(ns, allowed);
                p.getInventory().setItem(ps, notAllowed);
                this.notify(p, EventTypes.HOLD, notAllowed);
            }
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        ItemStack stack = event.getItem().getItemStack();
        PlayerInventory inv = p.getInventory();
        if (Perms.NOHOLD.has(p, stack)) {
            if (inv.getItemInHand().getTypeId() == 0
                    && inv.firstEmpty() == inv.getHeldItemSlot()) {
                event.setCancelled(true);
                event.getItem().setPickupDelay(200);
                this.notify(p, EventTypes.HOLD, stack);
            }
        }
    }

    private void notify(Player p, EventTypes type, ItemStack stack) {
        StringHelper.notifyPlayer(p, type, stack);
        StringHelper.notifyAdmin(p, type, stack);
    }
}
