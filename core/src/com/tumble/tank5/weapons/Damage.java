package com.tumble.tank5.weapons;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.game_object.tiles.Tile.TileType;
import com.tumble.tank5.util.GameError;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameObject;


/**
 * Utility class to store a single <code>GameObject</code> (to be) damaged in an event, and the amount of damage to take.
 * 
 * @author Tumbl
 *
 */
public class Damage {
	private GameObject victim;
	private int damage;
	private Position contactPoint;
	
	/**
	 * Constructs a <code>Damage</code> instance to store the amount of damage (to
	 * be) taken by a single target from some event, and the <code>GameObject</code>
	 * who received it (does not store the attacker).
	 * 
	 * @param victim       - the targeted GameObject. Must not be <code>null</code>
	 *                     or {@link TileType#AIR};
	 * 
	 * @param damage       - the amount of damage to deal to the <code>victim</code>
	 *                     (to subtract from their health via their
	 *                     {@link GameObject#damage(int, Entity)} method).
	 * 
	 * @param contactPoint - the place where the bullet entered the target
	 *                     <code>Tile</code> or <code>Entity</code> (if the damage
	 *                     was dealt by a bullet - otherwise <code>null</code>).
	 * 
	 * @throws GameError if the <code>victim</code> was <code>null</code> or
	 *                   {@link TileType#AIR}.
	 */
	public Damage(GameObject victim, int damage, Position contactPoint) {
		if (victim == null)
			throw new GameError("A Damage's victim cannot be null!");
		if (victim instanceof Tile && ((Tile) victim).getType() == TileType.AIR)
			throw new GameError("A Damage's victim cannot be air!");
		
		this.victim = victim;
		this.damage = damage;
		this.contactPoint = contactPoint;
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
	
	/**
	 * Gets the <code>Position</code> where the line of the damaging bullet first
	 * contacted the victim (if the damage was dealt by a bullet).
	 * 
	 * @return the <code>Position</code> of the first contact point of the bullet if
	 *         applicable, otherwise <code>null</code>.
	 */
	public Position getContactPoint() {
		return contactPoint;
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
