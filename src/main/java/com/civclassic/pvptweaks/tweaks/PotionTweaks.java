package com.civclassic.pvptweaks.tweaks;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;

public class PotionTweaks extends Tweak {
	
	private double drinkableDurationMultiplier;
	private double splashDurationMultiplier;
	private double strengthMultiplier;
	private double instantHealthMultiplier;

	public PotionTweaks(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			for(PotionEffect effect : player.getActivePotionEffects()) {
				if(effect.getType() == PotionEffectType.INCREASE_DAMAGE) {
					int potionLevel = effect.getAmplifier() + 1;
					double base = event.getDamage() / (1.3 * potionLevel + 1);
					double newDamage = base + (potionLevel * strengthMultiplier);
					event.setDamage(newDamage);
					return;
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPotionSplash(PotionSplashEvent event) {
		event.getEntity().getEffects().forEach(effect -> {
			if(effect != null){
				for(LivingEntity entity : event.getAffectedEntities()) {
					if(!(entity instanceof Player)) continue;
					Player player = (Player) entity;
					double intensity = event.getIntensity(player);
					if(!effect.getType().isInstant()) {
						int newDuration = (int) (effect.getDuration() * splashDurationMultiplier * intensity);
						player.addPotionEffect(new PotionEffect(effect.getType(), newDuration, effect.getAmplifier()), true);
					}
				}
			}
		});
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getRegainReason() == RegainReason.MAGIC) {
			event.setAmount(event.getAmount() * instantHealthMultiplier);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if(item.getType() == Material.POTION && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
			PotionData data = ((PotionMeta) item.getItemMeta()).getBasePotionData();
			PotionEffectType type = data.getType().getEffectType();
			if(type == PotionEffectType.HARM || type == PotionEffectType.HEAL) return;
			int duration = (int) (getDuration(type, data.isExtended(), data.isUpgraded()) * drinkableDurationMultiplier);
			player.addPotionEffect(new PotionEffect(type, duration, data.isUpgraded() ? 1 : 0), true);
		}
	}
	
	private int getDuration(PotionEffectType type, boolean extended, boolean upgraded) {
		int base = 3600;
		if(extended) {
			base = 9600;
		} else if(upgraded) {
			base = 1800;
		}
		return (int) (base * type.getDurationModifier());
	}

	@Override
	public void loadConfig(ConfigurationSection config) {
		drinkableDurationMultiplier = config.getDouble("drinkableDurationMultiplier");
		splashDurationMultiplier = config.getDouble("splashDurationMultiplier");
		strengthMultiplier = config.getDouble("strengthMultiplier");
		instantHealthMultiplier = config.getDouble("instantHealthMultiplier");
	}

	@Override
	protected String status() {
		StringBuilder status = new StringBuilder();
		status.append("  drinkable duration multiplier: ").append(drinkableDurationMultiplier).append("\n");
		status.append("  splash duration multiplier: ").append(splashDurationMultiplier).append("\n");
		status.append("  strength modifier: ").append(strengthMultiplier).append("\n");
		status.append("  instant health multiplier: ").append(instantHealthMultiplier).append("\n");
		return status.toString();
	}
}
