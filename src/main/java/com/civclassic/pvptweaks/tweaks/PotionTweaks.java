package com.civclassic.pvptweaks.tweaks;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
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
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		PotionEffect effect = event.getEntity().getEffects().iterator().next();
		if(effect == null) return;
		for(LivingEntity entity : event.getAffectedEntities()) {
			if(!(entity instanceof Player)) continue;
			Player player = (Player) entity;
			double intensity = event.getIntensity(player);
			if(!effect.getType().isInstant()) {
				int newDuration = (int) (effect.getDuration() * splashDurationMultiplier * intensity);
				player.addPotionEffect(new PotionEffect(effect.getType(), newDuration, effect.getAmplifier()), true);
			} else if(effect.getType() == PotionEffectType.HEAL) {
				double base = 4.0 * (effect.getAmplifier() + 1) * intensity;
				double toAdd = (base * instantHealthMultiplier) - base;
				player.setHealth(Math.min(player.getHealth() + toAdd, player.getMaxHealth()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if(item.getType() == Material.POTION && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
			PotionData data = ((PotionMeta) item.getItemMeta()).getBasePotionData();
			PotionEffectType type = data.getType().getEffectType();
			if(type.isInstant()) return;
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

}
