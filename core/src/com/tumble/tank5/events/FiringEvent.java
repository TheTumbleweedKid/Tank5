package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class FiringEvent extends Event {
	private Entity attacker;
	private Position from, to;
	
	public FiringEvent(int tickNumber, Entity attacker, Position from, Position to) {
		super(tickNumber);
		
		this.attacker = attacker;
		
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick) {
		return !attacker.isDead() && gW.entityAt(from) == attacker && currentTick >= tickNumber;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (!attacker.isDead()) { // && attacker.getWeapon().ableToFire(roundNumber)) {
			eventStream.offer(
					new DamageEvent(
							attacker,
							attacker.getWeapon().fire(
									attacker.getID(),
									gW,
									from,
									to)));
			finished = true;
		}
	}
}
