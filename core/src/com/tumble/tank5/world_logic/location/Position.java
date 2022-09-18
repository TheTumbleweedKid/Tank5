package com.tumble.tank5.world_logic.location;

/**
 * Stores a location in the game world (could be off the board) in world-coordinates.
 * 
 * @author Tumble
 *
 */
public class Position {
	/**
	 * The world-coordinates of the <code>Position</code>.
	 */
	public final double x, y, z;
	
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns a new <code>Position</code> that has been moved from the location of this
	 * <code>Position</code> in a given <code>Direction</code> (remembering
	 * <code>Direction</code>s are limited to +/- 1 * {@link Tile.TILE_SIZE} in each axis
	 * direction).
	 * 
	 * @param dir the <code>Direction</code> to move from this <code>Position</code> in.
	 * 
	 * @return the moved <code>Position</code>.
	 */
	public Position move(Direction dir) {
		return new Position(x + dir.getX(), y + dir.getY(), z + dir.getZ());
	}
}
