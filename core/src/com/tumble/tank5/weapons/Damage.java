package com.tumble.tank5.weapons;

import com.tumble.tank5.world_logic.GameObject;

public class Damage {
	private GameObject victim;
	private int damage;
	
	public Damage(GameObject victim, int damage) {
		this.victim = victim;
		this.damage = damage;
	}

	/**
	 * Gets the victim <code>GameObject</code> (the damagee, you could say).
	 * 
	 * @return the victim of this damage.
	 */
	public GameObject getVictim() {
		return victim;
	}
	
	/**
	 * Gets the numerical damage suffered by the victim.
	 * 
	 * @return the amount of damage taken.
	 */
	public int getDamage() {
		return damage;
	}
}
