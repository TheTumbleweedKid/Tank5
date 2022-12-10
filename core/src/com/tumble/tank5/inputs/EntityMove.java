package com.tumble.tank5.inputs;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.game_object.Move;
import com.tumble.tank5.world_logic.DirectionVector;
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
	public boolean apply(Game g) {
		if (entity == null || entity.isDead()) return false;

		DirectionVector toMove = moveIn.combine(
				g.getMoves().get(entity).direction.asVector());
		
		if (toMove.equals(moveIn)) return false;
		
		if (!entity.canMove(toMove.asEnum(), g.getWorld())) return false;
		
		g.getMoves().put(
				entity,
				new Move(
						toMove.asEnum(),
						entity.getPosition().tileCentre(),
						entity.getPosition().tileCentre().move(toMove)
				)
		);
		return true;
	}
}
