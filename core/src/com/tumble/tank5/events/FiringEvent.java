package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

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
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return gW.hasEntity(attacker)
				&& !attacker.isDead()
				&& attacker.getWeapon() != null
				&& attacker.getWeapon().ableToFire(roundNumber)
				&& currentTick >= tickNumber;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (!attacker.isDead()) {
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

	@Override
	public String toString() {
		return "{FiringEvent["
				+ tickNumber
				+ "] ("
				+ attacker
				+ "): "
				+ from
				+ "->"
				+ to
				+ "}";
	}
}
