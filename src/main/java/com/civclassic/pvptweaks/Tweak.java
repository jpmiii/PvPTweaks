package com.civclassic.pvptweaks;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public abstract class Tweak implements Listener {

	public Tweak(PvPTweaks plugin, ConfigurationSection config) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		loadConfig(config);
	}
	
	public abstract void loadConfig(ConfigurationSection config);
}
