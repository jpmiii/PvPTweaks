package com.civclassic.pvptweaks.tweaks;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;

public class GeneralTweaks extends Tweak {

	private double healthRegen;
	
	public GeneralTweaks(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getRegainReason() != RegainReason.REGEN && event.getRegainReason() != RegainReason.SATIATED
				&& event.getEntity() instanceof Player) return;
		event.setAmount(healthRegen);
	}
	
	@Override
	public void loadConfig(ConfigurationSection config) {
		healthRegen = config.getDouble("healthRegen");
	}

}
