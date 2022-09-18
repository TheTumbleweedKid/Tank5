package com.tumble.tank5.world_logic.location;

import com.tumble.tank5.world_logic.Tile;

public class Direction {
	public final int dx, dy, dz;
	
	public Direction(int x, int y, int z) {
		dx = x;
		dy = y;
		dz = z;
	}
	
	/**
	 * Gets the change in x-coordinate (in world-coordinates) of the <code>Direction</code>.
	 * 
	 * @return the change in east/west position.
	 */
	public double getX() {
		return dx * Tile.TILE_SIZE;
	}
	
	/**
	 * Gets the change in y-coordinate (in world-coordinates) of the <code>Direction</code>.
	 * 
	 * @return the change in north/south position.
	 */
	public double getY() {
		return dy * Tile.TILE_SIZE;
	}
	
	/**
	 * Gets the change in z-coordinate (in world-coordinates) of the <code>Direction</code>.
	 * 
	 * @return the change in altitude.
	 */
	public double getZ() {
		return dz * Tile.TILE_SIZE;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Direction addDeltas(int x, int y, int z) {
		return new Direction(
				ensureLength(dx + x),
				ensureLength(dy + y),
				ensureLength(dz + z));
	}
	
	private static int ensureLength(int delta) {
		return (int) Math.min(Math.abs(delta), 1) * (int) Math.signum(delta);
	}
}
