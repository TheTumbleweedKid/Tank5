package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class TriggerPullEvent extends Event {
	private Entity attacker;
	private Position[] positions;
	
	public TriggerPullEvent(Entity attacker, Position ... positions) {
		this.attacker = attacker;
		
		this.positions = positions;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick) {
		// Second condition only swaps one kind of inconsistent world state for another...
		return !attacker.isDead() && gW.entityAt(positions[0]) == attacker;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		for (FiringEvent fE : attacker.getWeapon().getFiringEvents(
				attacker.getID(),
				gW,
				positions)) {
			eventStream.addLast(fE);
		}
		finished = true;
	}
}
