package com.tumble.tank5.util;

import com.tumble.tank5.tiles.Tile;

/**
 * Stores a location in the game world (could be off the board) in
 * world-coordinates. Note that 'world-coordinates' differ from
 * '<code>Tile</code>-coordinates' in that <code>Tile</code>-coordinates are a
 * special subset of world-coordinates (ones that have only integral components,
 * corresponding to the centre of a <code>Tile</code>).
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
	 * Constructs a new <code>Position</code> from some given world-coordinates.
	 * 
	 * @param x - the x-coordinate (east/west location) of the
	 *          <code>Position</code>.
	 * 
	 * @param y - the y-coordinate (north/south location) of the
	 *          <code>Position</code>.
	 * 
	 * @param z - the z-coordinate (altitude, 'z-layer') of the
	 *          <code>Position</code>.
	 */
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Gets the x-coordinate of this <code>Position</code> in <code>Tile</code>
	 * coordinates. In other words, the x-coordinate of the <code>Tile</code> whose
	 * centre lies closest to this <code>Position</code> (i.e, that this
	 * <code>Position</code> lies within).
	 * 
	 * @return the east/west location of this <code>Position</code> (in integral
	 *         <code>Tile</code>-sized units).
	 */
	public int getX() {
		return (int) Math.floor(x / Tile.TILE_SIZE);
	}
	
	/**
	 * Gets the y-coordinate of this <code>Position</code> in <code>Tile</code>
	 * coordinates. In other words, the y-coordinate of the <code>Tile</code> whose
	 * centre lies closest to this <code>Position</code> (i.e, that this
	 * <code>Position</code> lies within).
	 * 
	 * @return the north/south location of this <code>Position</code> (in integral
	 *         <code>Tile</code>-sized units).
	 */
	public int getY() {
		return (int) Math.floor(y / Tile.TILE_SIZE);
	}
	
	/**
	 * Gets the z-coordinate of this <code>Position</code> in <code>Tile</code>
	 * coordinates. In other words, the z-coordinate of the <code>Tile</code> whose
	 * centre lies closest to this <code>Position</code> (i.e, that this
	 * <code>Position</code> lies within).
	 * 
	 * @return the altitude (z-layer) of this <code>Position</code> (in integral
	 *         <code>Tile</code>-sized units).
	 */
	public int getZ() {
		return (int) Math.floor(z / Tile.TILE_SIZE);
	}
	
	public Position move(DirectionVector.Direction dir) {
		return move(dir.asVector());
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
	
	public Position tileCentre() {
		return new Position(
				(getX() + 0.5) * Tile.TILE_SIZE,
				(getY() + 0.5) * Tile.TILE_SIZE,
				(getZ() + 0.5) * Tile.TILE_SIZE);
	}
	
	public boolean sameTile(Position other) {
		return other != null && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
	}
	
	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
	}
}
