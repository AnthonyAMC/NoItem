package net.worldoftomorrow.nala.ni.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.Container;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.InventoryCrafting;
import net.worldoftomorrow.nala.ni.CustomBlocks;
import net.worldoftomorrow.nala.ni.EventTypes;
import net.worldoftomorrow.nala.ni.Log;
import net.worldoftomorrow.nala.ni.Perms;
import net.worldoftomorrow.nala.ni.StringHelper;
import net.worldoftomorrow.nala.ni.CustomItems.CustomBlock;
import net.worldoftomorrow.nala.ni.CustomItems.CustomFurnace;
import net.worldoftomorrow.nala.ni.CustomItems.CustomWorkbench;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import forge.bukkit.ModInventoryView;

public class InventoryListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
		Inventory inv = event.getInventory();

		switch (inv.getType()) {
		case CRAFTING:
			this.handleCrafting(event, p);
			break;
		case BREWING:
			this.handleBrewing(event, p);
			break;
		case WORKBENCH:
			this.handleWorkbench(event, p);
			break;
		case FURNACE:
			this.handleFurnace(event, p);
			break;
		case ENCHANTING:
			this.handleEnchanting(event, p);
			break;
		case CHEST:
			this.handleChest(event, p);
			break;
		case DISPENSER:
			this.handleDispenser(event, p);
			break;
		default:
			this.handleGenericInv(event, p);
			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity entity = event.getPlayer();
		Player p = Bukkit.getPlayer(entity.getName());
		List<Block> blocks = entity.getLastTwoTargetBlocks(null, 8);
		if (!blocks.isEmpty() && blocks.size() == 2) {
			Block target = blocks.get(1);
			if (Perms.NOOPEN.has(p, target)) {
				event.setCancelled(true);
				// TODO: find a fix for the chest sticking open
				int id = target.getTypeId();
				byte data = target.getData();
				this.notify(Bukkit.getPlayer(entity.getName()), EventTypes.OPEN, new ItemStack(id, data));
				return;
			}
			//Check for custom items that open without a block
			ItemStack inHand = p.getItemInHand();
			if(CustomBlocks.isCustomBlock(inHand.getTypeId(), inHand.getDurability())) {
				if(Perms.NOOPEN.has(p, inHand)) {
					event.setCancelled(true);
					this.notify(Bukkit.getPlayer(entity.getName()), EventTypes.OPEN, inHand);
					return;
				}
			}
		}
	}

	private void handleCrafting(InventoryClickEvent event, Player p) {
		int rs = event.getRawSlot();
		ItemStack stack = null;
		if (rs >= 0) {
			stack = event.getCurrentItem();
		}
		SlotType st = event.getSlotType();
		// NoCraft
		if (st == SlotType.RESULT && stack != null && Perms.NOCRAFT.has(p, stack)) {
			event.setCancelled(true);
			this.notify(p, EventTypes.CRAFT, stack);
			return;
		}
		// NoWear
		if (st.equals(SlotType.ARMOR)) {
			ItemStack oncur = p.getItemOnCursor();
			if (oncur != null) {
				if (Perms.NOWEAR.has(p, oncur)) {
					event.setCancelled(true);
					this.notify(p, EventTypes.WEAR, oncur);
					event.getView().setItem(event.getRawSlot(), null);
					return;
				}
			}
		}

		ItemStack clicked = event.getCurrentItem();
		if (clicked != null && event.isShiftClick()) {
			if (Perms.NOWEAR.has(p, clicked)) {
				event.setCancelled(true);
				this.notify(p, EventTypes.WEAR, clicked);
				return;
			}
		}
		// NoHold
		this.handleNoHold(event, p);
	}

	private void handleBrewing(InventoryClickEvent event, Player p) {
		int rs = event.getRawSlot();
		ItemStack oncur = p.getItemOnCursor();
		Inventory inv = event.getInventory();

		// NoBrew
		if (rs == 3 && oncur != null) {
			if (!this.checkCanBrew(inv, oncur, p)) {
				event.setCancelled(true);
				return;
			}
		} else if (rs < 3 && rs >= 0) {
			ItemStack ing = inv.getItem(3);
			if (ing != null && oncur != null) {
				int potiondv = oncur.getDurability();
				if (Perms.NOBREW.has(p, potiondv + "." + ing.getTypeId())) {
					event.setCancelled(true);
					String recipe = potiondv + ":" + ing.getTypeId();
					StringHelper.notifyPlayer(p, EventTypes.BREW, recipe);
					StringHelper.notifyAdmin(p, EventTypes.BREW, recipe);
					return;
				}
			}
		}

		// NoHold
		this.handleNoHold(event, p);
	}

	@EventHandler
	public void onItemCraft(CraftItemEvent event) {
		SlotType st = event.getSlotType();
		Inventory inv = event.getInventory();
		Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
		if (st == SlotType.RESULT) {
			if (inv.getItem(0) != null) {
				ItemStack stack = inv.getItem(0);
				if (Perms.NOCRAFT.has(p, stack)) {
					event.setCancelled(true);
					this.notify(p, EventTypes.CRAFT, stack);
				}
			}
		}

		// NoHold
		this.handleNoHold(event, p);
	}
	
	private void handleWorkbench(InventoryClickEvent event, Player p) {
		this.handleNoHold(event, p);
	}

	private void handleDispenser(InventoryClickEvent event, Player p) {
		// NoHold
		this.handleNoHold(event, p);
	}

	private void handleChest(InventoryClickEvent event, Player p) {
		this.handleGenericInv(event, p);
		// NoHold
		this.handleNoHold(event, p);
	}

	private void handleEnchanting(InventoryClickEvent event, Player p) {
		// NoHold
		this.handleNoHold(event, p);
	}

	private void handleFurnace(InventoryClickEvent event, Player p) {
		int rs = event.getRawSlot();
		ItemStack oncur = p.getItemOnCursor();
		if (rs == 0 && oncur != null) {
			if (Perms.NOCOOK.has(p, oncur)) {
				event.setCancelled(true);
				this.notify(p, EventTypes.COOK, oncur);
				return;
			}
		}
		// TODO: fuel slots
		// NoHold
		this.handleNoHold(event, p);
	}

	private void handleGenericInv(InventoryClickEvent event, Player p) {
		Block b = p.getTargetBlock(null, 8);
		//Log.debug("TargetBlock: " + b.getTypeId() + ", " + b.getData());
		// Custom block handling
		if (CustomBlocks.isCustomBlock(b.getTypeId(), b.getData())) {
			//Log.debug("is a custom block");
			int clicked = event.getRawSlot();
			InventoryView view = event.getView();
			CustomBlock cb = CustomBlocks.getCustomBlock(b.getTypeId(), b.getData());
			switch (cb.getType()) {
			case FURNACE:
				CustomFurnace cf = (CustomFurnace) cb;
				if (cf.isFuelSlot((short) clicked) && p.getItemOnCursor() != null) {
					for (Short s : cf.getItemSlots()) {
						ItemStack item = view.getItem(s);
						if (item != null && Perms.NOCOOK.has(p, item)) {
							event.setCancelled(true);
							this.notify(p, EventTypes.COOK, item);
							return;
						}
					}
				} else if (cf.isItemSlot((short) clicked)
						&& p.getItemOnCursor() != null) {
					List<ItemStack> fuels = new ArrayList<ItemStack>();
					// TODO: this can be optimized to not use a list; if fuel !=
					// null, check and return
					for (Short s : cf.getFuelSlots()) {
						ItemStack fuel = view.getItem(s);
						if (fuel != null) {
							fuels.add(fuel);
						}
					}
					if (!fuels.isEmpty()) {
						ItemStack onCur = p.getItemOnCursor();
						if (Perms.NOCOOK.has(p, onCur)) {
							event.setCancelled(true);
							this.notify(p, EventTypes.COOK, onCur);
							return;
						}
					}
				}
				break;
			case WORKBENCH:
				CustomWorkbench cw = (CustomWorkbench) cb;
				if (cw.isResultSlot((short) clicked)) {
					ItemStack result = view.getItem(clicked);
					if (result != null && Perms.NOCRAFT.has(p, result)) {
						event.setCancelled(true);
						this.notify(p, EventTypes.CRAFT, result);
						return;
					}
				} else if (cw.isRecipeSlot((short) clicked) && view.getItem(clicked) == null) {
					try {
						ModInventoryView miv = (ModInventoryView) view;
						Field fcontainer = view.getClass().getDeclaredField("container");
						fcontainer.setAccessible(true);
						Container container = (Container) fcontainer.get(miv);
						InventoryCrafting craftingInv = new InventoryCrafting(container, 3, 3); //3x3 for now
						craftingInv.resultInventory = new InventoryCraftResult();
						
						for (int i = 0; i < 9; i++) {
							short slot = (Short) cw.getRecipeSlots().toArray()[i];
							ItemStack item;
							if(slot == clicked)
								item = view.getCursor();
							else
								item = view.getItem(slot);
							
							if (item == null)
								continue;
							net.minecraft.server.ItemStack stack = new net.minecraft.server.ItemStack(
									item.getTypeId(), item.getAmount(),
									item.getDurability());
							craftingInv.setItem(i, stack);
						}
						
						net.minecraft.server.ItemStack mcResult = CraftingManager.getInstance().craft(craftingInv);
						if (mcResult == null)
							return;

						ItemStack result = new ItemStack(mcResult.id, mcResult.getData());

						if (Perms.NOCRAFT.has(p, result)) {
							event.setCancelled(true);
							this.notify(p, EventTypes.CRAFT, result);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				Log.severe("Undefined custom block.");
				break;
			}
		}

		this.handleNoHold(event, p);
	}

	private boolean checkCanBrew(Inventory inventory, ItemStack oncur, Player p) {

		int count = 0;
		for (ItemStack stack : inventory.getContents()) {
			if (count >= 3)
				break; // If we have checked all potion slots
			if (stack != null) {
				int dv = stack.getDurability();
				if (Perms.NOBREW.has(p, dv + "." + oncur.getTypeId())) {
					String recipe = dv + ":" + oncur.getTypeId();
					StringHelper.notifyPlayer(p, EventTypes.BREW, recipe);
					StringHelper.notifyAdmin(p, EventTypes.BREW, recipe);
					return false;
				}
			}
		}
		return true;
	}

	private void handleNoHold(InventoryClickEvent event, Player p) {
		if(event.isCancelled())
			return;

		ItemStack oncur = p.getItemOnCursor();
		SlotType st = event.getSlotType();
		ItemStack clicked = event.getCurrentItem();
		int slot = event.getSlot();
		
		if (st == SlotType.QUICKBAR) {
			int qbsel = p.getInventory().getHeldItemSlot();
			if (oncur != null && slot == qbsel) {
				if (Perms.NOHOLD.has(p, oncur)) {
					event.setCancelled(true);
					this.notify(p, EventTypes.HOLD, oncur);
					return;
				}
			}
		} else {
			if(clicked != null && event.isShiftClick()) {
				if(st == SlotType.RESULT)
					return;
				if(Perms.NOHOLD.has(p, clicked)) {
					event.setCancelled(true);
					this.notify(p, EventTypes.HOLD, clicked);
					return;
				}
			}
		}
	}

	private void notify(Player p, EventTypes type, ItemStack stack) {
		StringHelper.notifyPlayer(p, type, stack);
		StringHelper.notifyAdmin(p, type, stack);
	}
}
