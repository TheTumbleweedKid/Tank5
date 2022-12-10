package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.game_object.GameObject;
import com.tumble.tank5.tiles.Tile;
import com.tumble.tank5.world_logic.GameWorld;

public class DeathEvent extends Event {
	private GameObject victim;
	private Entity attacker;

	public DeathEvent(int tickNumber, GameObject victim, Entity attacker) {
		super(tickNumber);

		this.victim = victim;
		this.attacker = attacker;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (victim instanceof Entity) {
			// Record kill somehow!
		} else {
			((Tile) victim).die(attacker, gW, eventStream);
		}
		
	}
}

