package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;

public class Damage extends Event {
	private Entity attacker;
	private Pair<GameObject, Integer>[] damages;

	public Damage(Entity attacker, Pair<GameObject, Integer> ... damages) {
		this.attacker = attacker;
		this.damages = damages;
	}
	
	@Override
	public boolean applicable(GameWorld gW) {
		return true;
	}

	@Override
	public void apply(GameWorld gW, Queue<Event> eventStream) {
		for (Pair<GameObject, Integer> pair : damages) {
			if (pair.first().damage(pair.second(), attacker)) {
				// eventStream.add(new DeathEvent);
			}
		}
	}
}
