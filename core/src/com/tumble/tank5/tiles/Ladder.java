package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.DirectionVector;

public class Ladder extends Tile {
	private DirectionVector obstructiveDirection;
	
	public Ladder(int rotation) {
		type = TileType.LADDER;
		
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
	public boolean providesSupport() {
		return true;
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
		// TODO Auto-generated method stub
		return null;
	}

}
