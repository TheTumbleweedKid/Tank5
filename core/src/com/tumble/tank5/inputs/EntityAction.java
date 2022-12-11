package com.tumble.tank5.inputs;

import com.tumble.tank5.entities.Action;
import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.world_logic.Game;

/**
 * Represents a suggested action input (either shooting, switching weapon or
 * reloading) for an <code>Entity</code> to perform in a <code>Round</code>.
 * 
 * @author Tumbl
 *
 */
public class EntityAction extends Input {
	private Entity entity;
	private Action action;
	
	public EntityAction(long time, Entity entity, Action action) {
		this.time = time;
		
		this.entity = entity;
		this.action = action.copy();
	}
	
	@Override
	public Pair<Entity, Object> apply(Game g) {
		if (entity == null || entity.isDead() || g == null || !g.getWorld().hasEntity(entity))
			return null;

		boolean valid = false;
		switch (action.getType()) {
		case FIRE:
			valid = entity.canAttack(g, action.getPositions());
			break;
		case SWITCH_WEAPON:
			valid = entity.canSwitchWeapon();
			break;
		case RELOAD:
			valid = entity.canReload();
			break;
		case NONE:
			// No effect. The default Action is of ActionType.NONE, so there is no point
			// changing it.
		}

		return valid ? new Pair<Entity, Object>(entity, action.copy()) : null;
	}
}
