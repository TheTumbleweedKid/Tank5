package com.tumble.tank5.inputs;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

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
	private Position[] positions;
	
	public enum Action {
		FIRE,
		SWITCH_WEAPON,
		RELOAD
	}
	
	public EntityAction(long time, Entity entity, Action action, Position... positions) {
		this.time = time;
		
		this.entity = entity;
		this.action = action;
		this.positions = positions;
	}
	
	@Override
	public boolean apply(GameWorld gW) {
		switch (action) {
		case FIRE:
			return entity.addAttack(gW, positions);
		case SWITCH_WEAPON:
			return entity.addWeaponSwitch(gW);
		case RELOAD:
			return entity.addReload(gW);
		default:
			return false;
		}
	}
}
