package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.DirectionVector;

public class Wall extends Tile {

	public Wall() {
		type = TileType.WALL;
	}
	
	@Override
	public boolean isObstruction(DirectionVector dir) {
		return true;
	}

	@Override
	public boolean providesSupport() {
		return true;
	}

	@Override
	public void makeRubble(GameWorld gW) {
		
	}

	@Override
	public boolean isRubble() {
		return false;
	}

	@Override
	public String toString() {
		return "W";
	}

}
