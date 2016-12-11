package com.civclassic.pvptweaks.util;

import java.util.HashMap;
import java.util.Map;

public class MillisecondCooldownHandler<E> implements ICooldownHandler<E> {

	private Map<E, Long> cds;
	
	private long cooldown;
	
	public MillisecondCooldownHandler(long cooldown) {
		this.cooldown = cooldown;
		cds = new HashMap<E, Long>();
	}
	
	@Override
	public void putOnCooldown(E e) {
		cds.put(e, System.currentTimeMillis());
	}

	@Override
	public boolean onCooldown(E e) {
		Long last = cds.get(e);
		return last != null && System.currentTimeMillis() - last <= cooldown;
	}

	@Override
	public long getRemainingCooldown(E e) {
		Long last = cds.get(e);
		if(last == null) return 0;
		long left = System.currentTimeMillis() - last;
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
