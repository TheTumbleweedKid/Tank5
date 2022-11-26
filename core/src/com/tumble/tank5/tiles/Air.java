package com.tumble.tank5.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.DirectionVector;

/**
 * Represents an empty <code>Tile</code> in the <code>GameWorld</code>. Air
 * cannot obstruct, support or be climbed upon by any <code>GameObject</code>,
 * nor can it form rubble (because it is air, of course!). This is a singleton
 * class (as there is no point in having lots of identical air
 * <code>Tile</code>s.
 * 
 * @author Tumble
 *
 */
public class Air extends Tile {
	public static Air AIR = new Air();
	
	private Air() {
		super(TileType.AIR, null, 0, 0);
	}

	@Override
	public boolean isObstruction(DirectionVector dir) {
		return false;
	}

	@Override
	public boolean stopsBullets() {
		return false;
	}

	@Override
	public boolean stopsFalling() {
		return false;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return " ";
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int hash = toString().hashCode();
		
		// Is obstruction.
		hash = hash * prime + 0;
		// Stops bullets.
		hash = hash * prime + 0;
		// Stops falling.
		hash = hash * prime + 0;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return false;
	}
}
