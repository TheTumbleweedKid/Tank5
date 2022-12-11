package com.tumble.tank5.events;

import java.util.Queue;

import com.tumble.tank5.world_logic.GameWorld;

/**
 * A single event that happens in a <code>Round</code>.
 * 
 * @author Tumbl
 *
 */
public abstract class Event implements Comparable<Event> {
	public final int tickNumber;
	protected boolean finished = false;
	
	public Event(int tickNumber) {
		this.tickNumber = tickNumber;
	}
	
	public int compareTo(Event other) {
		return tickNumber - other.tickNumber;
	}
	
	public abstract boolean applicable(GameWorld gW, int currentTick);
	
	public abstract void apply(GameWorld gW, int currentTick, Queue<Event> eventStream);
	
	public final boolean isFinished() {
		return finished;
	}
}
