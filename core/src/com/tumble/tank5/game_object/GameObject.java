package com.tumble.tank5.game_object;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.Position;

public abstract class GameObject {
	protected Position position;
	
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
	
	/**
	 * Permanently subtracts a given amount of damage from the
	 * <code>GameObject</code>'s health pool.
	 * 
	 * @param damage   - the amount of damage to take. Values <= 0 will have no
	 *                 effect.
	 * 
	 * @param attacker - the <code>Entity</code> who caused the damage to be
	 *                 inflicted (directly or indirectly).
	 * 
	 * @return <code>true</code> if this <code>GameObject</code> now has 0 health or
	 *         less, otherwise <code>false</code>.
	 */
	public final boolean damage(int damage, Entity attacker) {
		if (damage <= 0) {
			health -= damage;
			this.attacker = attacker;
		}
		
		return health <= 0;
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
