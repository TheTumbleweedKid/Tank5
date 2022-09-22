package com.tumble.tank5.world_logic;

import com.tumble.tank5.tiles.Tile;

/**
 * Stores a direction (only cares about those directions that an
 * <code>Entity</code> can move in):
 * <li>any of the 4 cardinal compass points, or the 4 midway points between
 * them</li>
 * <li>either of the <code>Tile</code>s immediately above or below (if the
 * <code>Entity</code> is on a <code>Ladder</code>)</li>
 * <li>nowhere (if they stay on the <code>Tile</code> they are already on).</li>
 * 
 * Note that  the <code>x</code> <code>y</code> and
 * <code>z</code> fields are limited to values of only -1, 0 or 1.
 * 
 * @author Tumbl
 *
 */
public class DirectionVector {
	/**
	 * The <code>Tile</code>-count movement components of the
	 * <code>DirectionVector</code> (can be either -1, 0 or 1).
	 */
	public final int x, y, z;
	
	public enum Direction {
		N,		// North
		NE,		// North-east
		E,		// East
		SE,		// South-east
		S,		// South
		SW,		// South-west
		W,		// West
		NW,		// North-west
		UP,		// +1 z-layer
		DOWN,	// -1 z-layer
		NONE;	// Stationary
		
		public static Direction asEnum(DirectionVector v) {
			if (v.z == 1) {
				return UP;
			} else if (v.z == -1) {
				return DOWN;
			} else {
				if (v.x == 1) {
					if (v.y == 1) {
						return NE;
					} else if (v.y == -1) {
						return SE;
					} else {
						return E;
					}
				} else if (v.x == -1) {
					if (v.y == 1) {
						return NW;
					} else if (v.y == -1) {
						return SW;
					} else {
						return W;
					}
				} else {
					if (v.y == 1) {
						return N;
					} else if (v.y == -1) {
						return S;
					} else {
						return NONE;
					}
				}
			}
		}
	}

	/**
	 * Construct a new <code>DirectionVector</code> from the given vector components
	 * (measured in the number of <code>Tile</code>s to move in each direction).
	 * 
	 * @param x how many <code>Tile</code>s to move in the east/west direction.
	 * 
	 * @param y how many <code>Tile</code>s to move in the north/south direction.
	 * 
	 * @param z how many <code>Tile</code>s (i.e., z-layers) to change the altitude.
	 */
	public DirectionVector(int x, int y, int z) {
		if (Math.abs(z) == 0) {
			this.x = ensureLength(x);
			this.y = ensureLength(y);
			this.z = 0;
		} else {
			this.x = 0;
			this.y = 0;
			this.z = ensureLength(z);
		}
	}

	/**
	 * Gets the x-component of this <code>DirectionVector</code> in world-coordinates.
	 * 
	 * @return the change in east/west position.
	 */
	public double getX() {
		return x * Tile.TILE_SIZE;
	}

	/**
	 * Gets the y-component of this <code>DirectionVector</code> in world-coordinates.
	 * 
	 * @return the change in north/south position.
	 */
	public double getY() {
		return y * Tile.TILE_SIZE;
	}

	/**
	 * Gets the z-component of this <code>DirectionVector</code> in world-coordinates.
	 * 
	 * @return the change in altitude (z-layer) position.
	 */
	public double getZ() {
		return z * Tile.TILE_SIZE;
	}
	
	/**
	 * Return a new <code>DirectionVector</code> that is a combination of this
	 * <code>DirectionVector</code> and some given x-, y- and z-components.
	 * 
	 * @param x the x-component to add (number of <code>Tile</code>s).
	 * @param y the y-component to add (number of <code>Tile</code>s).
	 * @param z the z-component to add (number of <code>Tile</code>s).
	 * 
	 * @return the combined <code>DirectionVector</code>.
	 */
	public DirectionVector combine(int x, int y, int z) {
		return new DirectionVector(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;

		DirectionVector other = (DirectionVector) obj;

		return x == other.x && y == other.y && z == other.z;
	}

	@Override
	public int hashCode() {
		int prime = 43;

		int hash = getClass().hashCode();

		hash = hash * prime + x;
		hash = hash * prime + y;
		hash = hash * prime + z;

		return hash;
	}

	private static int ensureLength(int delta) {
		return (int) Math.min(Math.abs(delta), 1) * (int) Math.signum(delta);
	}
}
