package net.worldoftomorrow.nala.ni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private final File config;
	private final YamlConfiguration messages;
	private final YamlConfiguration notify;
	private final YamlConfiguration misc;
	
	private final NoItem plugin;

	public Config(NoItem plugin) {
		
		this.plugin = plugin;
		this.messages = YamlConfiguration.loadConfiguration(plugin.getResource("messages.yml"));
		this.notify = YamlConfiguration.loadConfiguration(plugin.getResource("notify.yml"));
		this.misc = YamlConfiguration.loadConfiguration(plugin.getResource("misc.yml"));
		this.config = new File(plugin.getDataFolder(), "config.yml");
		
		if(!this.config.exists()) {
			this.copyDefaultFile(true);
			return;
		} else {
			final Map<String, Object> values = plugin.getConfig().getValues(true);
			for(String key : values.keySet()) {
				if(key.startsWith("Messages")) {
					this.messages.set(key, values.get(key));
				} else if (key.startsWith("Notify")) {
					this.notify.set(key, values.get(key));
				} else {
					this.misc.set(key, values.get(key));
				}
			}
			this.copyDefaultFile(false);
		}
	}

	private void copyDefaultFile(boolean create) {
		try {
			if(create && !plugin.getDataFolder().mkdir() && !config.createNewFile()) {
				Log.severe("Could not create configuration file!");
			}
			PrintWriter out = new PrintWriter(config);
			out.write(this.messages.saveToString());
			out.write(this.notify.saveToString());
			out.write(this.misc.saveToString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object getValue(String key) {
		return NoItem.getPlugin().getConfig().get(key);
	}

	public static boolean getBoolean(String key) {
		return NoItem.getPlugin().getConfig().getBoolean(key);
	}

	public static String getString(String key) {
		return NoItem.getPlugin().getConfig().getString(key);
	}

	public static Map<String, Object> getValues() {
		return NoItem.getPlugin().getConfig().getValues(true);
	}
	
	
}
