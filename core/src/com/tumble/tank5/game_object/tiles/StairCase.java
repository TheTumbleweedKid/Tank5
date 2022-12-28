package com.tumble.tank5.game_object.tiles;

import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.DirectionVector.Direction;
import com.tumble.tank5.util.Position;

public class StairCase extends Tile {
	public final DirectionVector upDirection, downDirection;
	
	public StairCase(Position pos, DirectionVector up) {
		super(TileType.STAIRS, pos, 20, 3);
		
		upDirection = validate(up);
		downDirection = upDirection.reverse();
	}
	
	private DirectionVector validate(DirectionVector dir) {
		if (dir == null || dir.z != 0 || (dir.x != 0) == (dir.y != 0)) {
			return new DirectionVector(0, 0, 0);
		}
		
		return dir;
	}
	
	public DirectionVector getHeightChange(DirectionVector dir) {
		if (upDirection.equals(dir)) {
			return Direction.UP.asVector();
		} else if (downDirection.equals(dir)) {
			return Direction.DOWN.asVector();
		}
		
		return Direction.NONE.asVector();
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return !(upDirection.equals(dir) || downDirection.equals(dir) || Direction.NONE.asVector().equals(dir));
	}

	@Override
	public boolean stopsBullets() {
		return true;
	}

	@Override
	public boolean stopsFalling() {
		return false;
	}

	@Override
	public String toString() {
		switch (upDirection.asEnum()) {
		case N:
			return "v";
		case E:
			return "<";
		case S:
			return "^";
		case W:
			return ">";
		default:
			return "O";
		}
	}

}
