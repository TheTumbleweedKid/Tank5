package com.tumble.tank5.events;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameWorld;

public class EntityAttack implements Input {
	private Entity entity;

	@Override
	public boolean apply(GameWorld gW) {
		if (entity != null && !entity.isDead()) {
			return entity.setAttack();
		}
		return false;
	}
}
