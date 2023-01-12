package com.tumble.tank5.game_object.entities;

import java.util.Queue;

import com.tumble.tank5.events.Event;
import com.tumble.tank5.util.Position;
import com.tumble.tank5.util.DirectionVector.Direction;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;

public class Move {
	public final Direction direction;
	public final Position start, end;
	
	private boolean interrupted = false;

	public Move(Direction direction, Position start, Position end) {
		this.direction = direction != null ? direction : Direction.NONE;

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
		subject.setPosition(start);
	}
	
	public void applyMiddle(Entity subject, GameWorld gW, int currentTick, Queue<Event> eventStream) {
		if (!interrupted) {
			if (subject.canStep(this, gW)) {
				subject.setPosition(subject.getPosition().step(direction, 1));
			} else {
				interrupted = true;
			}
		}
	}
	
	public void applyEnd(Entity subject) {
		if (!interrupted) {
			subject.setPosition(end);
		}
	}
	
	@Override
	public String toString() {
		return "<Move: " + start + "->" + direction + "->" + end + ">";
	}
}
