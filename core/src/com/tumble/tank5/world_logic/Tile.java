package com.tumble.tank5.world_logic;

import com.tumble.tank5.world_logic.location.Direction;

/**
 * Controls/stores the properties a tile on the game board.
 * 
 * @author Tumble
 *
 */
public abstract class Tile {
	public static final double TILE_SIZE = 10.0;
	
	/**
	 * Whether an object can move into the Tile in a given Direction (the obstructiveness may
	 * depend on the direction of motion for Stairs and Ladders, etc.).
	 * 
	 * @param dir the Direction the object is moving in.
	 * 
	 * @return <code>true</code> if an object can't move into the Tile in the given
	 * Direction, or <code>false</code> if can.
	 */
	public abstract boolean isObstruction(Direction dir);
	
	public abstract boolean providesSupport();
	
	public abstract boolean isLadder();
	
	public abstract void makeRubble(GameWorld gW);
	
	public abstract boolean isRubble();
	
	public abstract String toString();
}
