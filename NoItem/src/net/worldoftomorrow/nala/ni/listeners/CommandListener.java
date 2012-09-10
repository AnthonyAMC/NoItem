package net.worldoftomorrow.nala.ni.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.worldoftomorrow.nala.ni.Config;
import net.worldoftomorrow.nala.ni.NoItem;
import net.worldoftomorrow.nala.ni.Perms;
import net.worldoftomorrow.nala.ni.CustomItems.CustomBlockLoader;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class CommandListener implements CommandExecutor {
	NoItem plugin;

	public CommandListener(NoItem plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (args.length <= 0) {
			if (this.isPlayer(sender)) {
				Player p = (Player) sender;
				if (!Perms.ADMIN.has(p)) {
					sender.sendMessage(ChatColor.RED + "[NI] "
							+ "You do not have permission to use this command!");
					return true;
				}
			}
			sender.sendMessage(ChatColor.RED + "[NI] "
					+ "This server is running NoItem version: "
					+ ChatColor.UNDERLINE + plugin.getDescription().getVersion());
			return false;
		} else {
			if (args[0].equalsIgnoreCase("reload")) {
				if (this.isPlayer(sender) && !Perms.ADMIN.has((Player) sender)) {
					sender.sendMessage(ChatColor.RED + "[NI] "
							+ "You do not have permission to use this command!");
					return true;
				}
				plugin.getConfigClass().reloadConfig();
				if (Config.debugging() && plugin.debugListener == null) {
					PluginManager pm = this.plugin.getServer().getPluginManager();
					plugin.debugListener = new DebugListeners();
					pm.registerEvents(plugin.debugListener, plugin);
				} else if (!Config.debugging() && plugin.debugListener != null) {
					HandlerList.unregisterAll(plugin.debugListener);
					plugin.debugListener = null;
				}
				new CustomBlockLoader(plugin).load();
				sender.sendMessage(ChatColor.RED + "[NI] " + ChatColor.BLUE
						+ "Configuration reloaded.");
				return true;
			} else if (args[0].equalsIgnoreCase("claim")) {
				if (!this.isPlayer(sender)) {
					sender.sendMessage(ChatColor.RED
							+ "[NI] You must be a player to run this command.");
					return true;
				}
				Player p = (Player) sender;
				if (plugin.getItemList().containsKey(p.getName())) {
					List<ItemStack> remainder = new ArrayList<ItemStack>();
					for (ItemStack item : plugin.getItemList().get(p.getName())) {
						Map<Integer, ItemStack> left = p.getInventory()
								.addItem(item);
						if (!remainder.isEmpty()) {
							remainder.addAll(left.values());
						}
					}
					if (!remainder.isEmpty()) {
						sender.sendMessage(ChatColor.BLUE + "You still have "
								+ remainder.size() + " unclaimed items!");
						sender.sendMessage(ChatColor.BLUE
								+ "Type \"/noitem claim\" to get the rest!");
						plugin.getItemList().put(p.getName(), remainder);
					}
				} else {
					sender.sendMessage(ChatColor.RED
							+ "There are no items for you to claim!");
				}
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
}
