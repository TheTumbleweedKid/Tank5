package com.tumble.tank5.world_logic;

import com.tumble.tank5.entities.Entity;

public abstract class GameObject {
	private Position position;
	
	private int health = 0;
	private Entity attacker;
	
	protected final void spawn(Position position, int health) {
		this.position = new Position(position.x, position.y, position.z);
		this.health = health;
		attacker = null;
	}
	
	public final boolean damage(int damage, Entity attacker) {
		if (damage <= 0) return false;
		
		health -= damage;
		this.attacker = attacker;
		
		return health <= 0;
	}
	
	public final int getHealth() {
		return health;
	}
	
	public final Entity getAttacker() {
		return attacker;
	}
}
