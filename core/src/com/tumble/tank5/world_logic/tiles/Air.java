package com.tumble.tank5.world_logic.tiles;

import com.tumble.tank5.world_logic.GameWorld;
import com.tumble.tank5.world_logic.Tile;
import com.tumble.tank5.world_logic.location.Direction;

/**
 * Represents an empty <code>Tile</code> in the <code>GameWorld</code>. Air cannot obstruct, support
 * or be climbed upon by other entities or <code>Tile</code>s, nor can it form rubble (because it is
 * air, of course!).
 * 
 * @author Tumble
 *
 */
public class Air extends Tile {
	public static Air AIR = new Air();
	
	private Air() {
		
	}

	@Override
	public boolean isObstruction(Direction dir) {
		return false;
	}

	@Override
	public boolean providesSupport() {
		return false;
	}

	@Override
	public void makeRubble(GameWorld gW) {
		// Air can't be turned into rubble!
	}

	@Override
	public boolean isLadder() {
		return false;
	}

	@Override
	public boolean isRubble() {
		return false;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return " ";
	}
}
