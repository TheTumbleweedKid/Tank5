package com.tumble.tank5.events;

import com.badlogic.gdx.utils.Queue;

import com.tumble.tank5.world_logic.GameWorld;

/**
 * A single event that happens in a <code>Round</code>.
 * 
 * @author Tumbl
 *
 */
public abstract class Event {
	protected boolean finished = false;
	
	public abstract boolean applicable(GameWorld gW);
	
	public abstract void apply(GameWorld gW, Queue<Event> eventStream);
	
	public final boolean isFinished() {
		return finished;
	}
}
