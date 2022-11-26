package com.tumble.tank5.weapons;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.tiles.Tile.TileType;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.world_logic.GameObject;


/**
 * Utility class to store a single <code>GameObject</code> (to be) damaged in an event, and the amount of damage to take.
 * 
 * @author Tumbl
 *
 */
public class Damage {
	private GameObject victim;
	private int damage;
	
	/**
	 * Constructs a <code>Damage</code> instance to store the amount of damage (to
	 * be) taken by a single target from some event, and the <code>GameObject</code>
	 * who received it (does not store the attacker).
	 * 
	 * @param victim - the targeted GameObject. Must not be <code>null</code> or
	 *               {@link TileType#AIR};
	 * 
	 * @param damage - the amount of damage to deal to the <code>victim</code> (to
	 *               subtract from their health via their
	 *               {@link GameObject#damage(int, Entity)} method).
	 * 
	 * @throws GameError if the <code>victim</code> was <code>null</code> or
	 *                   {@link TileType#AIR}.
	 */
	public Damage(GameObject victim, int damage) {
		if (victim == null)
			throw new GameError("A Damage's victim cannot be null!");
		if (victim instanceof Tile && ((Tile) victim).getType() == TileType.AIR)
			throw new GameError("A Damage's victim cannot be air!");
		
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) return false;
		
		Damage other = (Damage) obj;
		
		return victim.equals(other.victim) && damage == other.damage;
	}
	
	@Override
	public int hashCode() {
		int prime = 23;
		int hash = getClass().hashCode();
		
		hash = hash * prime + victim.hashCode();
		
		hash = hash * prime + damage;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return "<" +
				victim +
				"(E)," +
				victim.getPosition() +
				"," +
				victim.getHealth() +
				"-" +
				damage +
				">";
	}
}
