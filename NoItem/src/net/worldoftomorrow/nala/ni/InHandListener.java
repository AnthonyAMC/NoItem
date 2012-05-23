package net.worldoftomorrow.nala.ni;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class InHandListener implements Listener{
	
	Log log = new Log();
	
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent event){
		log.debug("PlayerItemHeldEvent fired");
		
		Player p = event.getPlayer();
		int ns = event.getNewSlot();
		int ps = event.getPreviousSlot();
		int iid = 0;
		int dv = 0;
		ItemStack notAllowed = null;
		ItemStack allowed = null;
		
		if(p.getInventory().getItem(ns) != null){
			iid = p.getInventory().getItem(ns).getTypeId();
			dv = p.getInventory().getItem(ns).getDurability();
			notAllowed = p.getInventory().getItem(ns);
		}
		if(p.getInventory().getItem(ps) != null){
			allowed = p.getInventory().getItem(ps);
		}
		//Switch the items.
		if(Perms.NOHOLD.has(p, iid, dv)){
			p.getInventory().setItem(ns, allowed);
			p.getInventory().setItem(ps, notAllowed);
			if(Configuration.notifyNoHold()){
				StringHelper.notifyPlayer(p, Configuration.noHoldMessage(), iid);
			}
			if(Configuration.notifyAdmins()){
				StringHelper.notifyAdmin(p, Configuration.adminMessage(), iid);
			}
		}
	}
	
	@EventHandler
	public void onItemInvPlace(InventoryClickEvent event){
		if(event.getSlotType().equals(SlotType.QUICKBAR)){
			Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
			int iid = 0;
			int dv = 0;
			if(event.getCursor() != null){
				iid = event.getCursor().getTypeId();
				dv = event.getCursor().getDurability();
			}
			if(Perms.NOHOLD.has(p, iid, dv)){
				event.setCancelled(true);
				if(Configuration.notifyNoHold()){
					StringHelper.notifyPlayer(p, Configuration.noHoldMessage(), iid);
				}
				if(Configuration.notifyAdmins()){
					StringHelper.notifyAdmin(p, Configuration.adminMessage(), iid);
				}
			}
		}
	}
}
