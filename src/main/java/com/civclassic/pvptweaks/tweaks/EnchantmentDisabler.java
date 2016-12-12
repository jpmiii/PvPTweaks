package com.civclassic.pvptweaks.tweaks;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;

public class EnchantmentDisabler extends Tweak {

	private Set<Enchantment> disabled;
	
	public EnchantmentDisabler(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
		disabled = new HashSet<Enchantment>();
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		removeDisabledEnchants(event.getCurrentItem());
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.getItem() != null) {
			removeDisabledEnchants(event.getItem().getItemStack());
		}
	}
	
	private void removeDisabledEnchants(ItemStack item) {
		if(item != null && item.getEnchantments().size() != 0) {
			ItemMeta meta = item.getItemMeta();
			for(Enchantment enchant : meta.getEnchants().keySet()) {
				if(disabled.contains(enchant)) {
					item.removeEnchantment(enchant);
				}
			}
		}
	}

	@Override
	public void loadConfig(ConfigurationSection config) {
		for(String name : config.getStringList("disabled")) {
			Enchantment enchant = Enchantment.getByName(name);
			if(enchant != null) {
				disabled.add(enchant);
			}
		}
	}

	@Override
	protected String status() {
		StringBuilder status = new StringBuilder();
		status.append("  Disabled enchants: \n");
		for(Enchantment enchant : disabled) {
			status.append("    ").append(enchant.getName()).append("\n");
		}
		return status.toString();
	}
}
