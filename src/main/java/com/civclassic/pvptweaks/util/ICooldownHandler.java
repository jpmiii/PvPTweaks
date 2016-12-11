package com.civclassic.pvptweaks.util;

public interface ICooldownHandler<E> {

	void putOnCooldown(E e);
	
	boolean onCooldown(E e);
	
	long getRemainingCooldown(E e);
	
	long getTotalCooldown();
}
