package com.tumble.tank5.world_logic;

/**
 * Stores a location in the game world (could be off the board) in
 * world-coordinates.
 * 
 * @author Tumble
 *
 */
public class Position {
	/**
	 * The world-coordinates of the <code>Position</code>.
	 */
	public final double x, y, z;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns a new <code>Position</code> that has been moved from the location of
	 * this <code>Position</code> by a given <code>DirectionVector</code> (see
	 * {@link DirectionVector} for specifics of motion).
	 * 
	 * @param dir - the <code>DirectionVector</code> to move from this
	 *            <code>Position</code> in.
	 * 
	 * @return the moved <code>Position</code>.
	 */
	public Position move(DirectionVector dir) {
		return new Position(x + dir.getX(), y + dir.getY(), z + dir.getZ());
	}
}
