package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.util.Pair;
import com.tumble.tank5.weapons.Damage;
import com.tumble.tank5.world_logic.GameObject;
import com.tumble.tank5.world_logic.GameWorld;

public class DamageEvent extends Event {
	private Entity attacker;
	private Damage[] damages;

	public DamageEvent(Entity attacker, Damage ... damages) {
		this.attacker = attacker;
		this.damages = damages;
	}
	
	@Override
	public boolean applicable(GameWorld gW, int currentTick) {
		return true;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		for (Damage damage : damages) {
			if (damage.getVictim().damage(damage.getDamage(), attacker)) {
				// eventStream.add(new DeathEvent);
			}
		}
	}
}
