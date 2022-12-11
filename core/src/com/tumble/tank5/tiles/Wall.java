package com.tumble.tank5.tiles;

import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.Position;

public class Wall extends Tile {

	public Wall(Position pos) {
		super(TileType.WALL, pos, 45, 4);
	}
	
	@Override
	public boolean isObstruction(DirectionVector dir) {
		return true;
	}
	
	@Override
	public boolean stopsBullets() {
		return true;
	}

	@Override
	public boolean stopsFalling() {
		return true;
	}

	@Override
	public String toString() {
		return "W";
	}

}
