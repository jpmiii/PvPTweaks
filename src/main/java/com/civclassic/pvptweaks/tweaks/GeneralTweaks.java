package com.civclassic.pvptweaks.tweaks;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;

import vg.civcraft.mc.civmodcore.util.cooldowns.ICoolDownHandler;
import vg.civcraft.mc.civmodcore.util.cooldowns.MilliSecCoolDownHandler;

public class GeneralTweaks extends Tweak {

	private double healthRegen;
	private ICoolDownHandler<UUID> regenCds;
	
	public GeneralTweaks(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getRegainReason() != RegainReason.REGEN && event.getRegainReason() != RegainReason.SATIATED
				&& event.getEntityType() != EntityType.PLAYER) return;
		Player player = (Player) event.getEntity();
		if(regenCds.onCoolDown(player.getUniqueId())) {
			event.setCancelled(true);
		} else {
			event.setAmount(healthRegen);
			regenCds.putOnCoolDown(player.getUniqueId());
		}
	}
	
	@Override
	public void loadConfig(ConfigurationSection config) {
		healthRegen = config.getDouble("healthRegen");
		regenCds = new MilliSecCoolDownHandler<UUID>(config.getLong("regenDelay"));
	}

	@Override
	protected String status() {
		StringBuilder status = new StringBuilder();
		status.append("  healthRegen: ").append(healthRegen);
		return status.toString();
	}
}
