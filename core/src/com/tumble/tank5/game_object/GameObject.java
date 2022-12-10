package com.tumble.tank5.game_object;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.Position;

public abstract class GameObject {
	private Position position;
	
	private int health = 0;
	private Entity attacker;
	
	/**
	 * Spawn the <code>GameObject</code> at a given <code>Position</code>, with a
	 * given amount of starting health.
	 * 
	 * @param position - the <code>Position</code> to spawn at. May be
	 *                 <code>null</code> in the case of the singleton
	 *                 {@link Air#AIR} <code>Tile</code>.
	 * 
	 * @param health   - the health to start with. May be 0 in the case of {@link Air#AIR}.
	 */
	protected final void spawn(Position position, int health) {
		this.position = position;
		this.health = health;
		attacker = null;
	}
	
	public final boolean damage(int damage, Entity attacker) {
		if (damage <= 0) return false;
		
		health -= damage;
		this.attacker = attacker;
		
		return health <= 0;
	}
	
	final void setPosition(Position newPosition) {
		// Package-private.
		if (newPosition != null) position = newPosition;
	}
	
	public final Position getPosition() {
		return position;
	}
	
	public final int getHealth() {
		return health;
	}
	
	public final Entity getAttacker() {
		return attacker;
	}
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
}
