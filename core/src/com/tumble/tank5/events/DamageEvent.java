package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.weapons.Damage;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

public class DamageEvent extends Event {
	private Entity attacker;
	private Damage[] damages;

	public DamageEvent(Entity attacker, Damage ... damages) {
		super(-1); // To be applied ASAP!
		
		this.attacker = attacker;
		this.damages = damages;
	}
	
	@Override
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return true;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		for (Damage damage : damages) {
			if (damage.getVictim().damage(damage.getDamage(), attacker)) {
				eventStream.add(
						new DeathEvent(
								currentTick,
								damage.getVictim(),
								attacker));
			}
		}
	}
	
	@Override
	public String toString() {
		String res = "{DamageEvent (" 
				+ attacker
				+ "): ";
		
		for (Damage damage : damages) res += damage + ", ";

		return res.substring(0, res.length() - 2) + "}";
	}
}
