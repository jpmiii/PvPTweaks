package com.civclassic.pvptweaks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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
		
		ConfigurationSection config = getConfig().getConfigurationSection("tweaks");
		
		try {
			ClassPath getSamplersPath = ClassPath.from(getClassLoader());
			
			for(ClassPath.ClassInfo info : getSamplersPath.getTopLevelClasses("com.civclassic.pvptweaks.tweaks")) {
				try {
					Class<?> clazz = info.load();
					if(clazz != null && Tweak.class.isAssignableFrom(clazz)) {
						ConfigurationSection adjusterConfig = config.getConfigurationSection(clazz.getSimpleName());
						if(adjusterConfig != null) {
							Tweak adjuster = null;
							try {
								Constructor<?> construct = clazz.getConstructor(PvPTweaks.class, ConfigurationSection.class);
								adjuster = (Tweak) construct.newInstance(this, adjusterConfig);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(adjuster != null) {
								tweaks.put(clazz.getSimpleName(), adjuster);
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
	
	public static PvPTweaks getInstance() {
		return instance;
	}
}
