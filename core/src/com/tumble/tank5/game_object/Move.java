package com.tumble.tank5.game_object;

import java.util.Queue;

import com.tumble.tank5.entities.Entity;
import com.tumble.tank5.events.Event;
import com.tumble.tank5.world_logic.DirectionVector.Direction;
import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Position;

public class Move {
	public final Direction direction;
	public final Position start, end;
	
	private boolean interrupted = false;

	public Move(Direction direction, Position start, Position end) {
		this.direction = direction;

		this.start = start;
		this.end = end;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}
	
	public void interrupt() {
		interrupted = true;
	}
	
	public void applyStart(Entity subject) {
		// Cannot be interrupted yet.
		((GameObject) subject).setPosition(start);
	}
	
	public void applyMiddle(Entity subject, GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (!interrupted) {
			if (subject.canStep(this, gW)) {
				
			}
		}
	}
	
	public void applyEnd(Entity subject) {
		if (!interrupted) {
			((GameObject) subject).setPosition(end);
		}
	}
}
