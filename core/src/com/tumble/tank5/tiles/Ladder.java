package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.Position;
import com.tumble.tank5.world_logic.DirectionVector;

public class Ladder extends Tile {
	private DirectionVector obstructiveDirection;
	
	public Ladder(Position pos, int rotation) {
		super(TileType.LADDER, pos, 10, 1);
		
		switch (rotation) {
		case 1:
			obstructiveDirection = new DirectionVector(0, 1, 0);
			break;
		case 2:
			obstructiveDirection = new DirectionVector(-1, 0, 0);
			break;
		case 3:
			obstructiveDirection = new DirectionVector(0, -1, 0);
			break;
		default:
			obstructiveDirection = new DirectionVector(1, 0, 0);
		}
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return obstructiveDirection.equals(dir);
	}

	@Override
	public boolean stopsBullets() {
		return false;
	}

	@Override
	public boolean stopsFalling() {
		return true;
	}

	@Override
	public String toString() {
		return "#";
	}
}
