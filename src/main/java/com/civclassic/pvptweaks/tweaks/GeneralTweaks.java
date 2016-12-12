package com.civclassic.pvptweaks.tweaks;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;
import com.civclassic.pvptweaks.util.ICooldownHandler;
import com.civclassic.pvptweaks.util.MillisecondCooldownHandler;

public class GeneralTweaks extends Tweak {

	private double healthRegen;
	private ICooldownHandler<UUID> regenCds;
	
	public GeneralTweaks(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getRegainReason() != RegainReason.REGEN && event.getRegainReason() != RegainReason.SATIATED
				&& event.getEntity() instanceof Player) return;
		Player player = (Player) event.getEntity();
		if(regenCds.onCooldown(player.getUniqueId())) {
			event.setCancelled(true);
		} else {
			event.setAmount(healthRegen);
			regenCds.putOnCooldown(player.getUniqueId());
		}
	}
	
	@Override
	public void loadConfig(ConfigurationSection config) {
		healthRegen = config.getDouble("healthRegen");
		regenCds = new MillisecondCooldownHandler<UUID>(config.getLong("regenDelay"));
	}

	@Override
	protected String status() {
		StringBuilder status = new StringBuilder();
		status.append("  healthRegen: ").append(healthRegen);
		return status.toString();
	}
}
