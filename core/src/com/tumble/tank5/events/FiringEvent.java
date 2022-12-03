package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class FiringEvent extends Event {
	private Entity attacker;
	private int occurAtTick;
	private Position from, to;
	
	public FiringEvent(Entity attacker, int occurAtTick, Position from, Position to) {
		this.attacker = attacker;
		this.occurAtTick = occurAtTick;
		
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick) {
		return !attacker.isDead() && gW.entityAt(from) == attacker && currentTick >= occurAtTick;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		DamageEvent dE = new DamageEvent(
				attacker,
				attacker.getWeapon().fire(
						attacker.getID(),
						gW,
						from,
						to));
		finished = true;
	}
}
