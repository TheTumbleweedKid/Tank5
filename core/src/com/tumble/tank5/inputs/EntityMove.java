package com.tumble.tank5.inputs;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.world_logic.Game;

/**
 * Represents a suggested movement input for an <code>Entity</code> to perform
 * in a <code>Round</code>.
 * 
 * @author Tumbl
 *
 */
public class EntityMove extends Input {
	private Entity entity;
	private DirectionVector moveIn;
	
	public EntityMove(long time, Entity entity, DirectionVector moveIn) {
		this.time = time;
		
		this.entity = entity;
		this.moveIn = moveIn;
	}

	@Override
	public Pair<Entity, Object> apply(Game g) {
		if (entity == null || entity.isDead() || g == null || !g.getWorld().hasEntity(entity)) return null;

		DirectionVector toMove = moveIn.combine(g.getMove(entity));
		
		if (toMove.equals(moveIn) || !entity.canMove(toMove.asEnum(), g.getWorld())) return null;
		
		return new Pair<Entity, Object>(entity, toMove);
	}
}
