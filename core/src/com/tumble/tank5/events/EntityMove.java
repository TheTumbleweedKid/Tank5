package com.tumble.tank5.events;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.GameWorld;

public class EntityMove implements Input {
	private Entity entity;
	private DirectionVector moveIn;
	
	public EntityMove(Entity entity, DirectionVector moveIn) {
		this.entity = entity;
		this.moveIn = moveIn;
	}

	@Override
	public boolean apply(GameWorld gW) {
		if (entity == null || entity.isDead()) return false;
		
		return entity.addMove(moveIn, gW);
	}
}
