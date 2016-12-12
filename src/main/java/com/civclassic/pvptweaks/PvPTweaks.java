package com.civclassic.pvptweaks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.reflect.ClassPath;

public class PvPTweaks extends JavaPlugin {
	
	private static PvPTweaks instance;
	
	private Map<String, Tweak> tweaks;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		tweaks = new HashMap<String, Tweak>();
		
		getCommand("tweaks").setExecutor(this);
		
		ConfigurationSection config = getConfig().getConfigurationSection("tweaks");
		
		try {
			ClassPath getSamplersPath = ClassPath.from(getClassLoader());
			
			for(ClassPath.ClassInfo info : getSamplersPath.getTopLevelClasses("com.civclassic.pvptweaks.tweaks")) {
				try {
					Class<?> clazz = info.load();
					if(clazz != null && Tweak.class.isAssignableFrom(clazz)) {
						ConfigurationSection tweakConfig = config.getConfigurationSection(clazz.getSimpleName());
						if(tweakConfig != null) {
							Tweak tweak = null;
							try {
								Constructor<?> construct = clazz.getConstructor(PvPTweaks.class, ConfigurationSection.class);
								tweak = (Tweak) construct.newInstance(this, tweakConfig);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(tweak != null) {
								tweaks.put(clazz.getSimpleName(), tweak);
								tweak.enable();
							}
						}
					}
				} catch (NoClassDefFoundError e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		for(Tweak tweak : tweaks.values()) {
			tweak.disable();
		}
		tweaks.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			StringBuilder builder = new StringBuilder(ChatColor.GOLD + "List of tweaks:\n");
			for(String name : tweaks.keySet()) {
				Tweak tweak = tweaks.get(name);
				builder.append(tweak.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(name).append(", ");
			}
			String message = builder.toString();
			sender.sendMessage(message.substring(0, message.length() - 2));
		} else {
			if(args[0].equals("reload")) {
				getServer().getPluginManager().disablePlugin(this);;
				getServer().getPluginManager().enablePlugin(this);
			}
			Tweak tweak = tweaks.get(args[0]);
			if(tweak == null) {
				sender.sendMessage(ChatColor.RED + "Inavlid tweak!");
			} else {
				if(args.length == 1) {
					sender.sendMessage(tweak.getStatus());
				} else {
					if(args[1].toLowerCase().equals("enable")) {
						tweak.enable();
						sender.sendMessage(ChatColor.GREEN + args[0] + " enabled.");
					} else  if(args[1].toLowerCase().equals("disable")) {
						tweak.disable();
						sender.sendMessage(ChatColor.GREEN + args[0] + " disabled.");
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> possible = new LinkedList<String>();
		if(args.length == 0) {
			possible.addAll(tweaks.keySet());
		} else if(args.length == 1) {
			for(String tweak : tweaks.keySet()) {
				if(tweak.startsWith(args[0])) {
					possible.add(tweak);
				}
			}
		}
		return possible;
	}

	public static PvPTweaks getInstance() {
		return instance;
	}
}
