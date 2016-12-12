package com.civclassic.pvptweaks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Tweak implements Listener {

	private boolean enabled;
	private PvPTweaks plugin;
	private ConfigurationSection config;
	
	public Tweak(PvPTweaks plugin, ConfigurationSection config) {
		this.plugin = plugin;
		this.config = config;
	}
	
	public abstract void loadConfig(ConfigurationSection config);
	
	public void enable() {
		enabled = true;
		loadConfig(config);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void disable() {
		enabled = false;
		HandlerList.unregisterAll(this);
	}
	
	protected abstract String status();
	
	public String getStatus() {
		StringBuilder status = new StringBuilder();
		status.append(getClass().getSimpleName()).append(": ");
		if(isEnabled()) {
			status.append(ChatColor.GREEN).append("enabled.\n");
		} else {
			status.append(ChatColor.RED).append("disabled.\n");
		}
		status.append(ChatColor.RESET);
		status.append(status());
		return status.toString();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
}
