package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.DirectionVector.Direction;
import com.tumble.tank5.world_logic.GameWorld;

public class StairCase extends Tile {
	public final DirectionVector upDirection, downDirection;
	
	public StairCase(DirectionVector up) {
		type = TileType.STAIRS;
		
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
			return new DirectionVector(0, 0, 1);
		}
		
		return new DirectionVector(0, 0, 0);
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return upDirection.equals(dir) || downDirection.equals(dir);
	}

	@Override
	public boolean providesSupport() {
		return false;
	}

	@Override
	public void makeRubble(GameWorld gW) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRubble() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		switch (Direction.asEnum(upDirection)) {
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
