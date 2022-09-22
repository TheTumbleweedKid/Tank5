package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.DirectionVector;

/**
 * Controls/stores the properties of a tile on the game board.
 * 
 * @author Tumble
 *
 */
public abstract class Tile {
	public static final double TILE_SIZE = 10.0;

	/**
	 * Whether an object can move into the Tile in a given direction - i.e., via a
	 * given <code>DirectionVector</code> - (the obstructiveness may depend on the
	 * direction of approach for <code>Stairs</code> and <code>Ladders</code>,
	 * etc.).
	 * 
	 * @param dir the <code>DirectionVector</code> the object is moving toward the
	 *            <code>Tile</code> in.
	 * 
	 * @return <code>true</code> if an object can't move into the <code>Tile</code>
	 *         via the given <code>DirectionVector</code>, or <code>false</code> if can.
	 */
	public abstract boolean isObstruction(DirectionVector dir);

	public abstract boolean providesSupport();

	/**
	 * 
	 * @return
	 */
	public abstract boolean isClimable();

	public abstract void makeRubble(GameWorld gW);

	public abstract boolean isRubble();

	public abstract String toString();
}
