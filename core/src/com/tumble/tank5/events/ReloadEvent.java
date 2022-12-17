package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

public class ReloadEvent extends Event {
	private Entity reloader;

	public ReloadEvent(Entity reloader) {
		super(0);
		
		this.reloader = reloader;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return gW.hasEntity(reloader) && reloader.getWeapon() != null && reloader.getWeapon().ableToReload();
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		reloader.getWeapon().manualReload(currentTick);
	}

	@Override
	public String toString() {
		return "{ReloadEvent["
				+ tickNumber
				+ "] ("
				+ reloader
				+ ")}";
	}

}
