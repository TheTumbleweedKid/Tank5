package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

public class SwitchWeaponEvent extends Event {
	private Entity switcher;

	public SwitchWeaponEvent(Entity switcher) {
		super(0);
		
		this.switcher = switcher;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return gW.hasEntity(switcher)
				&& switcher.canSwitchWeapon();
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		switcher.switchWeapon();
		finished = true;
		
	}

	@Override
	public String toString() {
		return "{SwitchWeaponEvent["
				+ tickNumber
				+ "] ("
				+ switcher
				+ ")}";
	}
}
