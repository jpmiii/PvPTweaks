package com.civclassic.pvptweaks.tweaks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.civclassic.pvptweaks.PvPTweaks;
import com.civclassic.pvptweaks.Tweak;

import net.minecraft.server.v1_10_R1.NBTBase;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagDouble;
import net.minecraft.server.v1_10_R1.NBTTagInt;
import net.minecraft.server.v1_10_R1.NBTTagList;
import net.minecraft.server.v1_10_R1.NBTTagString;

public class WeaponTweaks extends Tweak {
	
	private Map<Material, WeaponConfig> weapons;

	public WeaponTweaks(PvPTweaks plugin, ConfigurationSection config) {
		super(plugin, config);
		weapons = new HashMap<Material, WeaponConfig>();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if(item == null) return;
		WeaponConfig config = weapons.get(item.getType());
		if(config == null) return;
		net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
		NBTBase base = nbt.get("AttributeModifiers");
		NBTTagList modifiers;
		if(base != null) {
			modifiers = (NBTTagList) base;
		} else {
			modifiers = new NBTTagList();
		}
		if(config.getDamage() != -1) {
			NBTTagCompound damage = new NBTTagCompound();
			damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
			damage.set("Name", new NBTTagString("generic.attackDamage"));
			damage.set("Operation", new NBTTagInt(0));
			damage.set("Amount", new NBTTagInt(config.getDamage()));
			damage.set("Slot", new NBTTagString("mainhand"));
			damage.set("UUIDLeast", new NBTTagInt(894654));
			damage.set("UUIDMost", new NBTTagInt(2872));
			modifiers.add(damage);
		}
		if(config.getAttackSpeed() != - 1.0) {
			NBTTagCompound speed = new NBTTagCompound();
			speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
			speed.set("Name", new NBTTagString("generic.attackSpeed"));
			speed.set("Operation", new NBTTagInt(0));
			speed.set("Amount", new NBTTagDouble(config.getAttackSpeed()));
			speed.set("Slot", new NBTTagString("mainhand"));
			speed.set("UUIDLeast", new NBTTagInt(894654));
			speed.set("UUIDMost", new NBTTagInt(2872));
			modifiers.add(speed);
		}
		nbt.set("AttributeModifiers", modifiers);
		nmsStack.setTag(nbt);
		ItemStack result = CraftItemStack.asBukkitCopy(nmsStack);
		event.setCurrentItem(result);
	}
	
	public void loadConfig(ConfigurationSection config) {
		for(String key : config.getKeys(false)) {
			Material mat = Material.getMaterial(key);
			ConfigurationSection weapon = config.getConfigurationSection(key);
			int damage = weapon.contains("damage") ? weapon.getInt("damage") : -1;
			double attackSpeed = weapon.contains("attackSpeed") ? weapon.getDouble("attackSpeed") : -1.0;
			if(damage == -1 && attackSpeed == -1.0) continue;
			WeaponConfig wc = new WeaponConfig(damage, attackSpeed);
			weapons.put(mat, wc);
		}
	}
	
	private class WeaponConfig {
		private int damage;
		private double attackSpeed;
		
		public WeaponConfig(int damage, double attackSpeed) {
			this.damage = damage;
			this.attackSpeed = attackSpeed;
		}
		
		public int getDamage() {
			return damage;
		}
		
		public double getAttackSpeed() {
			return attackSpeed;
		}
	}
}
