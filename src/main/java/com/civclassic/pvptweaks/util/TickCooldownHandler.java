package com.civclassic.pvptweaks.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.civclassic.pvptweaks.PvPTweaks;

public class TickCooldownHandler<E> implements ICooldownHandler<E> {
	
	private Map<E, Long> cds;
	
	private long cooldown;
	
	private long tickCounter;
	
	public TickCooldownHandler(long cooldown) {
		this.cooldown = cooldown;
		cds = new HashMap<E, Long>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPTweaks.getInstance(), new Runnable() {
			public void run() {
				tickCounter++;
			}
		}, 1l, 1l);
	}

	@Override
	public void putOnCooldown(E e) {
		cds.put(e, tickCounter);
	}

	@Override
	public boolean onCooldown(E e) {
		Long last = cds.get(e);
		return last != null && tickCounter - last <= cooldown;
	}

	@Override
	public long getRemainingCooldown(E e) {
		Long last = cds.get(e);
		if(last == null) {
			return 0;
		}
		long left = tickCounter - last;
		if(left < cooldown) {
			return cooldown - left;
		}
		return 0;
	}

	@Override
	public long getTotalCooldown() {
		return cooldown;
	}

}
