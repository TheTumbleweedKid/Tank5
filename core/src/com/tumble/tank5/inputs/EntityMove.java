package com.tumble.tank5.inputs;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.GameWorld;

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
	public boolean apply(GameWorld gW) {
		if (entity == null || entity.isDead()) return false;
		
		return entity.addMove(moveIn, gW);
	}
}
