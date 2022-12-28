package com.tumble.tank5.util;

import com.tumble.tank5.game_object.tiles.Tile;

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
		
		public DirectionVector asVector() {
			return new DirectionVector(this);
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
	
	private DirectionVector(Direction dir) {
		switch (dir) {
		case N:
			x = 0;
			y = 1;
			z = 0;
			break;
		case NE:
			x = 1;
			y = 1;
			z = 0;
			break;
		case E:
			x = 1;
			y = 0;
			z = 0;
			break;
		case SE:
			x = 1;
			y = -1;
			z = 0;
			break;
		case S:
			x = 0;
			y = -1;
			z = 0;
			break;
		case SW:
			x = -1;
			y = -1;
			z = 0;
			break;
		case W:
			x = -1;
			y = 0;
			z = 0;
			break;
		case NW:
			x = -1;
			y = 1;
			z = 0;
			break;
		case UP:
			x = 0;
			y = 0;
			z = 1;
			break;
		case DOWN:
			x = 0;
			y = 0;
			z = -1;
			break;
		default:
			// Direction.NONE.
			x = 0;
			y = 0;
			z = 0;
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
	 * Create a new <code>DirectionVector</code> that is a combination of this
	 * <code>DirectionVector</code> and another (via
	 * {@link DirectionVector#combine(int, int, int)}).
	 * 
	 * @param other - the <code>DirectionVector</code> to combine with this one.
	 * 
	 * @return the combined <code>DirectionVector</code>, or a new copy of this
	 *         <code>DirectionVector</code> if <code>other</code> was
	 *         <code>null</code>.
	 */
	public DirectionVector combine(DirectionVector other) {
		if (other == null) return new DirectionVector(x, y, z);
		
		return combine(other.x, other.y, other.z);
	}
	
	/**
	 * Create a new <code>DirectionVector</code> that is a combination of this
	 * <code>DirectionVector</code> and some given x-, y- and z-components.
	 * 
	 * @param x - the x-component to add (number of <code>Tile</code>s).
	 * 
	 * @param y - the y-component to add (number of <code>Tile</code>s).
	 * 
	 * @param z - the z-component to add (number of <code>Tile</code>s).
	 * 
	 * @return the combined <code>DirectionVector</code>.
	 */
	public DirectionVector combine(int x, int y, int z) {
		return new DirectionVector(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Create a new <code>DirectionVector</code> that represents the opposite
	 * direction to this one.
	 * 
	 * @return the reverse of this <code>DirectionVector</code>.
	 */
	public DirectionVector reverse() {
		return new DirectionVector(-x, -y, -z);
	}
	
	public Direction asEnum() {
		if (z > 0) {
			return Direction.UP;
		} else if (z < 0) {
			return Direction.DOWN;
		} else {
			if (x > 0) {
				if (y > 0) {
					return Direction.NE;
				} else if (y < 0) {
					return Direction.SE;
				} else {
					return Direction.E;
				}
			} else if (x < 0) {
				if (y > 0) {
					return Direction.NW;
				} else if (y < 0) {
					return Direction.SW;
				} else {
					return Direction.W;
				}
			} else {
				if (y > 0) {
					return Direction.N;
				} else if (y < 0) {
					return Direction.S;
				} else {
					return Direction.NONE;
				}
			}
		}
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

	/**
	 * Caps a given component-delta to values (i.e., lengths) of either -1, 0 or 1.
	 * 
	 * @param delta - the component to ensure length of.
	 * 
	 * @return a valid version of the component.
	 */
	private static int ensureLength(int delta) {
		return (int) Math.min(Math.abs(delta), 1) * (int) Math.signum(delta);
	}
}
