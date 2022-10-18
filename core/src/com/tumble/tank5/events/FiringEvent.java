package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class FiringEvent extends Event {
	private Entity attacker;
	private Position[] positions;
	
	public FiringEvent(Entity attacker, Position ... positions) {
		this.attacker = attacker;
		
		this.positions = positions;
	}

	@Override
	public boolean applicable(GameWorld gW) {
		return !attacker.isDead() && gW.entityAt(positions[0]) == attacker;
	}

	@Override
	public void apply(GameWorld gW, Queue<Event> eventStream) {
		attacker.getWeapon().fire(attacker.getID(), gW, positions);
		finished = true;
	}
}
