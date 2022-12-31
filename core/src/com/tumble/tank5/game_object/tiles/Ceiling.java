package com.tumble.tank5.game_object.tiles;

import com.tumble.tank5.util.DirectionVector;
import com.tumble.tank5.util.Position;

public class Ceiling extends Tile {

	public Ceiling(Position pos) {
		super(TileType.CEILING, pos, 35, 1);
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return dir.z != 0;
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
		return "C";
	}

}
