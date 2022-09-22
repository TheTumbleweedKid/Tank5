package com.tumble.tank5.events;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class EntityAction implements Input {
	private Entity entity;
	private Action action;
	private Position[] positions;
	
	public enum Action {
		FIRE,
		SWITCH_WEAPON,
		RELOAD
	}
	
	public EntityAction(Entity entity, Action action, Position... positions) {
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
