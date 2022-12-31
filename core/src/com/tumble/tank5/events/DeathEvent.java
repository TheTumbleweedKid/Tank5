package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.tiles.Tile;
import com.tumble.tank5.world_logic.game_n_world.GameObject;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

public class DeathEvent extends Event {
	private GameObject victim;
	private Entity attacker;

	public DeathEvent(int tickNumber, GameObject victim, Entity attacker) {
		super(tickNumber);

		this.victim = victim;
		this.attacker = attacker;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return currentTick >= tickNumber;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (victim instanceof Entity) {
			// Record kill somehow!
			gW.requestCorpsification((Entity) victim, attacker);
		} else {
			((Tile) victim).die(currentTick, attacker, gW, eventStream);
		}
		
		finished = true;
	}

	@Override
	public String toString() {
		return "{DeathEvent["
				+ tickNumber
				+ "] ("
				+ attacker
				+ "): "
				+ victim
				+ "}";
	}
}

