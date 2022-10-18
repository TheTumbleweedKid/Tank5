package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.DirectionVector;
import com.tumble.tank5.world_logic.Position;

public class Rubble extends Tile {
	private int pileSize;

	public Rubble(Tile tile, Position pos) {
		super(tile.getType(), pos, 0, tile.weight);
		pileSize = 0;
	}
	
	public void addToPile(Tile tile) {
		pileSize += tile.weight;
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return weight >= 5;
	}

	@Override
	public boolean stopsBullets() {
		return weight >= 5;
	}

	@Override
	public boolean stopsFalling() {
		return true;
	}

	@Override
	public String toString() {
		return "R";
	}

}
