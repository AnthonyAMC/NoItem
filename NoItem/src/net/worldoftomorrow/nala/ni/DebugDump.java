package net.worldoftomorrow.nala.ni;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.plugin.Plugin;

public class DebugDump {
	public static void dump(NoItem plugin) {
		File dump = new File(plugin.getDataFolder(), "debug_dump.txt");
		Log.info("----------------------------");
		Log.info("Dumping debug information...");
		if(dump.exists() && dump.delete())
			Log.info("Deleting old debug dump file...");
		try {
			if(!dump.createNewFile()) {
				Log.severe("Could not create dump file!");
				Log.info("----------------------------");
			}
			PrintWriter out = new PrintWriter(dump, "UTF-8");
			out.println("---- NoItem Debug Dump File ----");
			out.println("Plugin Version: " + plugin.getDescription().getVersion());
			out.println("CraftBukkit Version: " + plugin.getServer().getBukkitVersion());
			out.println("Minecraft Version: " + plugin.getServer().getVersion());
			out.println("Max Players: " + plugin.getServer().getMaxPlayers());
			out.println("Online Players: " + plugin.getServer().getOnlinePlayers().length);
			out.println("Online Mode: " + plugin.getServer().getOnlineMode());
			out.println("Using Vault: " + Vault.vaultPerms);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - HH:mm:ss zzzz");
			out.println("Date/Time: " + sdf.format(new Date(System.currentTimeMillis())));
			out.println("=================================");
			out.println("Item List Size: " + plugin.getItemList().size());
			out.println("Item List Values: ");
			if(!plugin.getItemList().isEmpty()) {
				for(String key : plugin.getItemList().keySet()) {
					out.println("    " + key + " - " + plugin.getItemList().get(key).size());
				}
			} else {
				out.println("    EMPTY");
			}
			out.println("=================================");
			out.println("--- Installed Plugins ---");
			Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
			List<Plugin> mods = new ArrayList<Plugin>();
			for(Plugin p : plugins) {
				String name = p.getName();
				String ver = p.getDescription().getVersion();
				if(name.startsWith("mod_") || ver.equalsIgnoreCase("forgemod")) {
					mods.add(p);
				} else {
					out.println("- " + name + " - " + ver);
				}
			}
			out.println("=================================");
			out.println("--- Installed Mods ---");
			for(Plugin p : mods) {
				out.println("- " + p.getName() + " - " + p.getDescription().getVersion());
			}
			out.println("=================================");
			out.println("--- Config Options ---");
			for(String key : Config.getValues().keySet()) {
				out.println(key + " - " + Config.getString(key));
			}
			out.println("=================================");
			out.println("--- END DEBUG DUMP ---");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.info("Debug dump completed...");
		Log.info("----------------------------");
	}
}
